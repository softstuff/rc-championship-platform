package rc.championship.mylaps.emulator.p3;

import eu.plib.P3tools.MsgProcessor;
import eu.plib.Ptools.Bytes;
import eu.plib.Ptools.Message;
import eu.plib.Ptools.MsgDetector;
import eu.plib.Ptools.ProtocolsEnum;
import org.apache.commons.codec.binary.Hex;

import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.Thread.sleep;

public class TcpIpSimulator {

    public static final String DATA_FILE = "P3data.json";
    public static final String INIT_FILE = "P3configdata.json";
    private static final String UDP_ADDRESS = "255.255.255.255";
    private static final int INPUT_PORT = 5403;
    private static final int OUTPUT_PORT = 5303;
    private static final long STATUS_TIMEOUT = 5000;
    public static OutputStream tcpOutputStream;
    private static SimulatorLogic logic;
    ExecutorService tcpWriter = Executors.newSingleThreadExecutor();
    ExecutorService tcpWriterPassings = Executors.newSingleThreadExecutor();
    ExecutorService udpWriter = Executors.newSingleThreadExecutor();
    private StatusThreadUdp statusThreadUdp;
    private StatusThread statusThread;
    private DatagramSocket udpSocket;
    private boolean udpActive = true;

    public TcpIpSimulator() {
        this(INIT_FILE);
    }

    public static void main(String[] args) {
        new TcpIpSimulator();
    }

    public TcpIpSimulator(String configFileName) {
        logic = new SimulatorLogic(configFileName, DATA_FILE);
        new TcpThread().start();
        new UdpThread().start();
    }

    private class TcpThread extends Thread {

        @Override
        public void run() {
            super.run();
            Thread.currentThread().setName("TCPIPRequestResponse");

            while (!Thread.currentThread().isInterrupted()) {
                Socket s = null;
                tcpOutputStream = null;
                ServerSocket socket = null;
                System.out.println("Started P3. Waiting for connection on port " + INPUT_PORT + "...");
                try {
                    socket = new ServerSocket(INPUT_PORT);
                    s = socket.accept();
                    tcpOutputStream = s.getOutputStream();
                    System.out.println("Starting TCPIP sender");
                    udpActive = false;
                    new TcpipReaderThread(s.getInputStream(), s).start();
                    statusThread = new StatusThread();
                    statusThread.start();
                    Future<Void> f1 = null, f2 = null;
                    while (true) {
                        f1 = tcpWriterPassings.submit(new TcpIpSenderAuto(tcpOutputStream));  // autogenerate records
                        f2 = tcpWriterPassings.submit(new TcpIpSenderTime(tcpOutputStream));
                        try {
                            for (int i = 0; i < 100; i++) {
                                if (f1.isDone() && f2.isDone())
                                    break;
                                sleep(100);
                            }
                        } catch (InterruptedException e) {
                            System.out.println("TCPIP sending takes too long");
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Unable to send data, error:" + e.getMessage());
//                System.exit(1);
                } finally {
                    if (tcpOutputStream != null) try {
                        tcpOutputStream.close();
                    } catch (IOException e) {
                    }
                    if (s != null) try {
                        s.close();
                    } catch (IOException e) {
                    }
                    if (socket != null) try {
                        socket.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

    public class TcpipReaderThread extends Thread {
        private final InputStream is;
        private final Socket socket;

        public TcpipReaderThread(InputStream inputStream, Socket socket) {
            this.is = inputStream;
            this.socket = socket;
        }

        /**
         * Reads incoming records and responds
         */
        @Override
        public void run() {
            super.run();
            Thread.currentThread().setName("TcpipReaderThread");

            while (!Thread.currentThread().isInterrupted()) {

                byte[] data = new byte[1024];
                int r = 0;
                try {
                    DataInputStream dis = new DataInputStream(is);
                    while (r < 1024) {
                        byte b = dis.readByte();
                        data[r] = b;
                        r++;
                        if (b == ProtocolsEnum.P3.getEor()[0]) break;
                    }

                    byte[] data2 = new byte[r];
                    System.arraycopy(data, 0, data2, 0, r);

                    System.out.println("Incoming command length:" + data2.length);
                    Bytes b = new Bytes();
                    b.add(data2);
                    if (MsgDetector.isComplete(b, ProtocolsEnum.P3) == null) {
                        System.err.println("Incomplete data received:" + Hex.encodeHexString(data2));
                    } else {
                        tcpWriter.submit(new TcpIpSenderResponse(data2, socket.getOutputStream()));
                        //         udpWriter.submit(new UdpSender(data2));
                    }

                } catch (EOFException eofe) {
                    System.out.println("Client disconnected EOF " + eofe);
                    System.exit(0);
                } catch (SocketException e) {
                    System.out.println("Client disconnected SE " + e);
                    System.exit(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private class UdpThread extends Thread {
        @Override
        public void run() {
            super.run();

            Thread.currentThread().setName("UDP listener");
            DatagramSocket serverSocket = null;
            try {
                serverSocket = new DatagramSocket(INPUT_PORT);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            udpSocket = serverSocket;

            udpSocket = serverSocket;
            statusThreadUdp = new StatusThreadUdp();
            statusThreadUdp.start();

            byte[] receiveData = new byte[1024];
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                if (serverSocket != null) {
                    try {
                        System.out.println("Listening on UDP port " + INPUT_PORT);
                        serverSocket.receive(receivePacket);
                        byte[] b = receivePacket.getData();
                        int l = receiveData.length;
                        if (l == b.length) l = gerFirstEor(b);
                        byte[] r = new byte[l];
                        System.arraycopy(b, 0, r, 0, r.length);
                        System.out.println("Received data length:" + l);
                        System.out.println("UDP Received:" + Hex.encodeHexString(r));
                        if (udpActive) udpWriter.submit(new UdpSender(r));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Socket is null");
                    System.exit(1);
                }
            }
        }
    }

    private int gerFirstEor(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            byte bb = b[i];
            if (bb == ProtocolsEnum.P3.getEor()[0])
                return i + 1;
        }
        return b.length;
    }


    private class TcpIpSenderTime implements Callable<Void> {
        private OutputStream os;

        public TcpIpSenderTime(OutputStream os) {
            this.os = os;
        }

        @Override
        public Void call() {
            Message response;
            System.out.println("Creating new Time record");
            Message timeResponse = logic.getTimeMessage(null);
            System.out.println("Time response:" + timeResponse);
            if (timeResponse == null) {
                System.err.println("No response found for Time record");
                return null;
            }
            byte[] b = new MsgProcessor(false).build(timeResponse);
            try {
                os.write(b);
            } catch (SocketException se) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    private class TcpIpSenderStatus implements Callable<Void> {
        private OutputStream os;

        public TcpIpSenderStatus(OutputStream os) {
            this.os = os;
        }

        @Override
        public Void call() {
            Message response = null;
            Message statusResponse = logic.getStatusMessage();
            response = logic.getStatusMessage();
            if (response == null) {
                System.err.println("No response found for Status record");
                return null;
            }
            System.out.println("Status:" + statusResponse);
            byte[] b = new MsgProcessor(false).build(response);
            try {
                os.write(b);
            } catch (SocketException se) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class TcpIpSenderResponse implements Callable<Void> {
        private byte[] b;
        private OutputStream os;

        public TcpIpSenderResponse(byte[] b, OutputStream os) {
            this.b = b;
            this.os = os;
        }

        @Override
        public Void call() {
            Message response;
            String request = "---";

            if (b == null)
                return null;

            Message parsed = new MsgProcessor(true).parse(b);
            request = parsed.toString();
            System.out.println("Creating record response to:" + parsed.getType().getSimpleName());
            List<Message> responses = logic.respondToCommand(parsed);
            if (responses.isEmpty()) {
                System.out.println("No response on TCP found. Responding with original");
                responses.add(parsed);
            }

            Iterator<Message> responsesI = responses.iterator();
            while (responsesI.hasNext()) {
                response = responsesI.next();

                b = new MsgProcessor(true).build(response);
                System.out.println("Sending response TCPIP:" + response.toString());

                try {
                    os.write(b);
                } catch (SocketException se) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    private class TcpIpSenderAuto implements Callable<Void> {
        private OutputStream os;

        public TcpIpSenderAuto(OutputStream os) {
            this.os = os;
            Thread.currentThread().setName("TcpIpSenderAuto");
        }

        @Override
        public Void call() {
            Message response;
            response = logic.getNextMsg();
            System.out.println("Created new passing record:" + response);
            if (response == null) {
                System.out.println("Delay " + 100 + "ms");
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                byte[] b = new MsgProcessor(false).build(response);
                try {
                    os.write(b);
                } catch (SocketException se) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    private class UdpSender implements Callable<Void> {
        private InetAddress address;
        private byte[] b;

        public UdpSender(byte[] b) {
            try {
                this.address = InetAddress.getByName(UDP_ADDRESS);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            this.b = b;
        }

        @Override
        public Void call() throws Exception {
            if (b == null) {
                System.err.println("Input data null. unable to respond");
                return null;
            }

            Message m = new MsgProcessor(true).parse(b);
            List<Message> responses = logic.respondToCommand(m);
            if (responses == null || responses.isEmpty()) {
                System.out.println("No response on UDP found. Responding with original");
                responses.add(m);
            }
            Iterator<Message> responsesI = responses.iterator();
            while (responsesI.hasNext()) {
                Message r = responsesI.next();
                byte[] bb = new MsgProcessor(true).build(r);
                DatagramPacket packet = new DatagramPacket(bb, bb.length);
                System.out.println("Sending UDP:" + Hex.encodeHexString(packet.getData()));
                DatagramPacket sendPacket = new DatagramPacket(bb, bb.length, address, OUTPUT_PORT);
                if (udpSocket != null) {
                    udpSocket.send(sendPacket);
                }
            }
            return null;
        }
    }

    private class UdpSenderStatus implements Callable<Void> {
        private InetAddress address;

        public UdpSenderStatus() {
            try {
                this.address = InetAddress.getByName(UDP_ADDRESS);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Void call() throws Exception {

            Message statusResponse = logic.getStatusMessage();
            if (statusResponse == null) {
                System.out.println("No status response found.");
                return null;
            }
            byte[] bb = new MsgProcessor(true).build(statusResponse);
            DatagramPacket packet = new DatagramPacket(bb, bb.length);
            System.out.println("Sending UDP:" + Hex.encodeHexString(packet.getData()));
            DatagramPacket sendPacket = new DatagramPacket(bb, bb.length, address, OUTPUT_PORT);
            if (udpSocket != null) {
                udpSocket.send(sendPacket);
            }
            return null;
        }
    }

    private class UdpSenderTime implements Callable<Void> {
        private InetAddress address;

        public UdpSenderTime() {
            try {
                this.address = InetAddress.getByName(UDP_ADDRESS);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Void call() throws Exception {
            Message timeResponse = logic.getTimeMessage(null);
            if (timeResponse == null) {
                System.out.println("No time response found.");
                return null;
            }
            byte[] bb = new MsgProcessor(true).build(timeResponse);
            DatagramPacket packet = new DatagramPacket(bb, bb.length);
            System.out.println("Sending UDP:" + Hex.encodeHexString(packet.getData()));
            DatagramPacket sendPacket = new DatagramPacket(bb, bb.length, address, OUTPUT_PORT);
            if (udpSocket != null) {
                udpSocket.send(sendPacket);
            }
            return null;
        }
    }

    private class StatusThread extends Thread {

        @Override
        public void run() {
            Thread.currentThread().setName("StatusThread");
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    tcpWriter.submit(new TcpIpSenderStatus(tcpOutputStream));
                    sleep(STATUS_TIMEOUT);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class StatusThreadUdp extends Thread {

        @Override
        public void run() {
            Thread.currentThread().setName("UDPStatus");
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    sleep(STATUS_TIMEOUT);
                    if (udpSocket != null && udpSocket.isBound())
                        if (udpActive) {
                            udpWriter.submit(new UdpSenderStatus());
                            udpWriter.submit(new UdpSenderTime());
                        }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
