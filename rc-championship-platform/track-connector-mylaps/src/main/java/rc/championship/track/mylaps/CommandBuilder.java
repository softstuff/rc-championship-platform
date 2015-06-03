package rc.championship.track.mylaps;

import java.nio.ByteBuffer;

/**
 *
 * @author Stefan
 */
public class CommandBuilder {
    public static final byte START_OF_RECORD  = (byte)0x8e;
    public static final byte END_OF_RECORD  = (byte)0x8f;
    public static final byte VERSION = 0x02;
    
    public static final byte TOR_RESET = 0x00;
public static final byte TOR_PASSING = 0x01;
public static final byte TOR_STATUS = 0x02;
public static final byte TOR_FIRST_CONTACT = 0x45;
public static final byte TOR_ERROR = (byte)0xff;
public static final byte TOR_VERSION = 0x03;
public static final byte TOR_RESEND = 0x04;
public static final byte TOR_CLEAR_PASSING = 0x05;
public static final byte TOR_WATCHDOG = 0x18;
public static final byte TOR_PING = 0x20;
public static final byte TOR_SIGNALS = 0x2d;
public static final byte TOR_SERVER_SETTINGS = 0x13;
public static final byte TOR_SESSION = 0x15;
public static final byte TOR_GENERAL_SETTINGS = 0x28;
public static final byte TOR_LOOP_TRIGGER = 0x2f;
public static final byte TOR_GPS_INFO = 0x30;
public static final byte TOR_TIMELINE = 0x4a;
public static final byte TOR_GET_TIME = 0x24;
public static final byte TOR_NETWORK_SETTINGS=0x16;
// TIMMING_SETTING = 0x12
    
    private ByteBuffer buffer = ByteBuffer.allocate(100);
    private int index = 0;
        
    private CommandBuilder addEndOfRecord(){
        buffer.put(index++, END_OF_RECORD);
        return this;
    }
    
    public ByteBuffer build(){
        addHeader();
        addEndOfRecord();
        return buffer.compact();
    }
    
    /**00 - SOR (Start of Record = 8e)
01 - Version (default = 02)
02 - length of record LSB
03 - length of record MSB
04 - CRC of record LSB
05 - CRC of record MSB
06 - Flags of record LSB
07 - Flags of record MSB
08 - TOR (Type of Record) LSB
09 - TOR (Type of Record) MSB
===========================
10 - FORs (Fields of Record)
*/
    private void addHeader(){
        byte[] length = ByteBuffer.allocate(2).putInt(index).array();
        byte[] crc = calcCrc16();
        byte[] flags = getFlagsOfRecord();
        byte[] tor = getTypeOfRecord();
        
        buffer.put(0, START_OF_RECORD);
        buffer.put(1, VERSION);
        buffer.put(2, length[1]);
        buffer.put(3, length[0]);
        buffer.put(4, crc[1]);
        buffer.put(5, crc[0]);
        buffer.put(6, flags[1]);
        buffer.put(7, flags[0]);
        buffer.put(8, tor[1]);
        buffer.put(9, tor[0]);        
        
    }

    private byte[] calcCrc16() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private byte[] getFlagsOfRecord() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private byte[] getTypeOfRecord() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
