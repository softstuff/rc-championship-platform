package rc.championship.mylaps.emulator.p3;

import eu.plib.P3tools.MsgProcessor;
import eu.plib.P3tools.data.msg.MsgCommon;
import eu.plib.P3tools.data.msg.v2.*;
import eu.plib.Ptools.Message;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static java.lang.Thread.sleep;

public class SimulatorLogic {

    private String dataFileName = null;
    private String initFileName = null;
    private BufferedReader inputDataFile = null;
    private final HashMap<String, Message> inits = new HashMap<String, Message>(10);
    private int lastPassingNumber=0;

    public SimulatorLogic(String myInitFile, String myDataFile) {
        System.out.println("Reading simulation data from file:" + myInitFile);
        this.initFileName = myInitFile;
        this.dataFileName = myDataFile;
        reopenInitFile();
    }

    private void reopenInitFile() {
        File f = new File(dataFileName);
        try {
            if (inputDataFile != null)
                inputDataFile.close();
            inputDataFile = new BufferedReader(new FileReader(f));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Unable to read file " + inputDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Message getNextMsg() {
        return readMsgFromFile();
    }

    private Message readMsgFromFile() {
        String line;

        line = readLineFromDataFile();
        if (line == null || line.length() == 0) {
            reopenInitFile();
            line = readLineFromDataFile();
        }

        if (line == null) throw new IllegalStateException("Unable to read data from input file!");

        if (line.startsWith("delay:")) {
            String[] items = line.split(":");
            long d = Long.parseLong(items[1].trim());
            try {
                System.out.println("Sleep: "+d+"ms");
                sleep(d);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            line = readLineFromDataFile();

        }
        Message msg = new MsgProcessor(false).parseJson(line);
        msg = updateDynamicFields(msg);
        return msg;
    }

    private Message updateDynamicFields(Message msg) {
        if (msg.getType().getSimpleName().equals("Passing")) {
            Message responseM;

            if (msg instanceof eu.plib.P3tools.data.msg.v0.Passing) {
                eu.plib.P3tools.data.msg.v0.Passing m = (eu.plib.P3tools.data.msg.v0.Passing) msg;
                m.passingNumber = String.valueOf(lastPassingNumber);
                m.RTC_Time = (int) System.currentTimeMillis()*1000;
                responseM=m;

            } else if (msg instanceof eu.plib.P3tools.data.msg.v1.Passing) {
                eu.plib.P3tools.data.msg.v1.Passing m = (eu.plib.P3tools.data.msg.v1.Passing) msg;
                m.passingNumber = lastPassingNumber;
                m.RTC_Time = System.currentTimeMillis()*1000;
                responseM=m;
            } else if (msg instanceof Passing) {
                Passing m = (Passing) msg;
                if (lastPassingNumber == 0) { // first, get passing from json data
                    lastPassingNumber = m.getPassingNumber();
                }
                m.passingNumber = lastPassingNumber;
                m.RTC_Time = System.currentTimeMillis()*1000;
                responseM=m;
            }  else {
                System.err.println("Unknown version of passing record:"+msg.getType().getName());
                responseM=msg;
            }
            lastPassingNumber++;
            return responseM;
        }
        return msg;
    }

    private String readLineFromDataFile() {
        try {
            while (true) {
                String line = inputDataFile.readLine();
                if (line == null) return null;
                if (!line.startsWith("//")) return line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized Message getTimeMessage(Long requestId) {
        Time t = new Time();
        if (requestId!=null) t.requestId=requestId;
        return respondToCommand(t).iterator().next();
    }

    public synchronized List<Message> respondToCommand(Message parsed) {
         if (inits.size() == 0 || parsed.getType().getSimpleName().equals("Passing"))  // TODO object copy
            readInits();

        List responses = new ArrayList<Message>();
        Message responseMsg = inits.get(parsed.getType().getSimpleName());
        if (parsed.getType().getSimpleName().equals("Unknown")) {
            System.err.println("Unknown message " + parsed.getType() + ", sending Error");
            responses.add(inits.get("Error"));
        } else if (responseMsg == null) {
            System.err.println("Unable to find response for message " + parsed.getType() + ", sending Error");
            responses.add(inits.get("Error"));
        }
        try {
            if (responseMsg.getType().getSimpleName().equals("Ping")) {  // TODO version check!
                eu.plib.P3tools.data.msg.v2.Ping pingResponse = (eu.plib.P3tools.data.msg.v2.Ping) responseMsg;
                eu.plib.P3tools.data.msg.v0.Ping pingRequest = (eu.plib.P3tools.data.msg.v0.Ping) parsed;
                pingResponse.dataEchoed = pingRequest.dataEchoed;
                pingResponse.requestId = pingRequest.requestId;
                responses.add(pingResponse);
            } else if (responseMsg.getType().getSimpleName().equals("Time")) {
                Time t = (Time) responseMsg;
                t.RTC_TIME = System.currentTimeMillis()*1000;
                t.requestId = ((MsgCommon)parsed).requestId;
                responses.add(t);
            } else if (responseMsg.getType().getSimpleName().equals("ResendPassings")) {
                responses.addAll(getResponseForResendPassings((eu.plib.P3tools.data.msg.v0.ResendPassings)parsed,(ResendPassings)responseMsg));
            } else if (responseMsg.getType().getSimpleName().equals("Passing")) {
                responses.add(responseMsg);
            } else
                responses.add(responseMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return responses;
    }

    private Collection getResponseForResendPassings(eu.plib.P3tools.data.msg.v0.ResendPassings requestRP, ResendPassings responseRP) {
        List<Message> responses = new ArrayList<Message>(2);
        for(int i=requestRP.from;i<=requestRP.until;i++) {        // TODO check max value
            List<Message> pSet = respondToCommand(new Passing());
            Passing p = (Passing) pSet.iterator().next();
            p.passingNumber=i;
            p.RTC_Time=System.currentTimeMillis()*1000;
            responses.add(p);
            System.out.println("Adding passing to RP:"+p);
        }
        responseRP.from = requestRP.from;
        responseRP.until = requestRP.until;
        responseRP.requestId = requestRP.requestId;
        responses.add(responseRP);
        System.out.println("Adding RP:" + responseRP);
        return responses;
    }

    private void readInits() {
        System.out.println("Opening init file " + initFileName);
        BufferedReader file = null;
        try {
            file = new BufferedReader(new FileReader(new File(initFileName)));
        } catch (FileNotFoundException e) {
            System.err.println("Unable to open init file " + initFileName);
            e.printStackTrace();
            System.exit(1);
        }
        MsgProcessor p = new MsgProcessor(false);
        System.out.println("Processing init file");
        while (true) {
            String line = null;
            try {
                line = file.readLine();
                if (line == null) break;
                Message m = p.parseJson(line);
                if (m != null) {
                    if (inits.get(m.getType()) != null)
                        System.err.println("Record " + m.getType().getSimpleName() + " is duplicated in configuration. Reduce.");
                    inits.put(m.getType().getSimpleName(), m);
                    if (m.getType().getSimpleName().equals("Session")) initSession(m);
                } else if (line != null && line.length() != 0)
                    System.err.println("Unable to parse line from config file:" + line);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        System.out.println("Records from config read:" + inits.size());

    }

    private void initSession(Message m) {
        Session s = (Session) m;
        lastPassingNumber = s.lastPassingNumber;
    }


    public Message getStatusMessage() {
        return respondToCommand(new Status()).iterator().next();
    }
}
