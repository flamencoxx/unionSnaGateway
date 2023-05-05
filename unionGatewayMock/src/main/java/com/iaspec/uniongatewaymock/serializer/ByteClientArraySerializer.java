package com.iaspec.uniongatewaymock.serializer;

import com.iaspec.uniongatewaymock.util.SystemLogger;
import org.springframework.integration.ip.tcp.serializer.AbstractPooledBufferByteArraySerializer;
import org.springframework.integration.ip.tcp.serializer.SoftEndOfStreamException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author Flamenco.xxx
 * @date 2022/9/22  17:41
 */
public class ByteClientArraySerializer extends AbstractPooledBufferByteArraySerializer {


    public ByteClientArraySerializer() {
        super();
    }

    public ByteClientArraySerializer(int maxMessageSize) {
        super();
        this.setMaxMessageSize(maxMessageSize);
    }

    @Override
    protected byte[] doDeserialize(InputStream inputStream, byte[] buffer) throws IOException {
        SystemLogger.debugMethod(getClass(), "doDeserialize", true, new String[] {});
        SystemLogger.info("doDeserialize2");
        int n = this.getBytesLen(inputStream, buffer);
        byte[] bytes;
        int deserializedMessageLength = this.getBytesLen(inputStream, buffer);
        if (deserializedMessageLength == 0) {
            // empty message, means the message is drop
            return null;
        } else {
            bytes = Arrays.copyOfRange(buffer, 4, deserializedMessageLength);
        }
        SystemLogger.debugMethod(getClass(), "doDeserialize", false, new String[] {});
        return bytes;
    }

    public int getBytes(InputStream inputStream, byte[] buffer) throws IOException {
        SystemLogger.debugMethod(getClass(), "getBytes", true, new String[] {});
        int n = 0;
        try {
            if (SystemLogger.isDebugEnabled()) {
                SystemLogger.debug("Available to read: " + inputStream.available());
            }
            n = inputStream.read(buffer);
//			SystemLogger.info("n={0}", n);
            if (n < 0) {
                throw new SoftEndOfStreamException("Stream closed between payloads");
            }

            if (n >= this.getMaxMessageSize()) {
                throw new IOException("exceed max message length: " + this.getMaxMessageSize());
            }

            return n;
        } catch (SoftEndOfStreamException e) {
            throw e;
        } catch (IOException e) {
            publishEvent(e, buffer, n);
            throw e;
        } catch (RuntimeException e) {
            publishEvent(e, buffer, n);
            throw e;
        } finally {
            SystemLogger.debugMethod(getClass(), "getBytes", false, new String[] {});
        }
    }

    public int fillToCrLf(InputStream inputStream, byte[] buffer) throws IOException {
        int n = 0;
        int bite;
        if (SystemLogger.isDebugEnabled()) {
            SystemLogger.debug("Available to read: " + inputStream.available());
        }
        try {
            while (true) {
                bite = inputStream.read();
                SystemLogger.info("bite= {0}", (int) bite);
                if (bite < 0 && n == 0) {
                    throw new SoftEndOfStreamException("Stream closed between payloads");
                }
                if (n < 0) {
                    break;
                }
                buffer[n++] = (byte) bite;
                if (n >= this.getMaxMessageSize()) {
                    throw new IOException("CRLF not found before max message length: " + this.getMaxMessageSize());
                }
            }
            return n;
        } catch (SoftEndOfStreamException e) {
            throw e;
        } catch (IOException e) {
            publishEvent(e, buffer, n);
            throw e;
        } catch (RuntimeException e) {
            publishEvent(e, buffer, n);
            throw e;
        }
    }

    public synchronized int getBytesLen(InputStream inputStream,byte[] buffer) throws IOException {
//        SystemLogger.debugMethod(getClass(), "getBytes", true, new String[] {});

        try{
            inputStream.read(buffer, 0, 4);

            byte[] theFirstFourBytes = Arrays.copyOfRange(buffer, 0, 4);

            if (isAllZero(theFirstFourBytes)){
                return 0;
//                throw new SoftEndOfStreamException("Data of length info is empty");
            }
            int targetMessageLength = Integer.parseInt(new String(theFirstFourBytes, StandardCharsets.US_ASCII));

            int realMessageLength = checkIntegrityAndPopulateBuffer(inputStream,targetMessageLength,4,buffer);

//            System.out.println("Server-targetMessageLength: " + targetMessageLength + "realMessageLength: " + realMessageLength);
            if (targetMessageLength != realMessageLength - 4) {
//                throw new SoftEndOfStreamException("Stream closed between payloads,targetMessageLength not equals realMessageLength");
                SystemLogger.error("targetMessageLength not equals realMessageLength",new String[]{},new Throwable("targetMessageLength not equals realMessageLength"));
                return 0;
            }
            return realMessageLength;
        } catch(NumberFormatException e) {
            SystemLogger.error("Please enter the message length for the first four digits, don input error format");
            return 0;
        }
        catch (SoftEndOfStreamException e) {
            throw e;
        } finally {
//            SystemLogger.debugMethod(getClass(), "getBytes", false, new String[] {});
        }

    }

    private int checkIntegrityAndPopulateBuffer(InputStream inputStream, int targetMessageLength,
                                                int receivedMessageCnt, byte[] buffer) throws IOException {
//        SystemLogger.debugMethod(getClass(), "checkIntegrityAndPopulateBuffer", true,
//                new String[] { "targetMessageLength", "receivedMessageCnt" }, targetMessageLength, receivedMessageCnt);
        try {
            if (targetMessageLength == receivedMessageCnt - 4) {
                return receivedMessageCnt;
            } else if (targetMessageLength > receivedMessageCnt - 4) {
                byte[] secoundBytes = new byte[this.getMaxMessageSize()];
                receivedMessageCnt = countinusToGetBytes2(inputStream, receivedMessageCnt, buffer, secoundBytes,targetMessageLength);
//                System.out.println("ServerReceivedMessageCnt: " + receivedMessageCnt);
                return checkIntegrityAndPopulateBuffer(inputStream, targetMessageLength, receivedMessageCnt, buffer);
            } else {
                /**
                 * 缓慢接受消息时不会有异常，但是并发接受时会进入这里 故注释此行
                 */
//                receivedMessageCnt = 0;
                return receivedMessageCnt;
            }
        } finally {
//            SystemLogger.debugMethod(getClass(), "checkIntegrityAndPopulateBuffer", false,
//                    new String[] { "targetMessageLength", "resultCnt" }, targetMessageLength, receivedMessageCnt);

        }
    }

    private int countinusToGetBytes2(InputStream inputStream, int receivedMessageCnt, byte[] buffer,byte[] secoundBytes, int targetLen)
            throws IOException {
        int secondLength = inputStream.read(secoundBytes,0,targetLen);

        if (receivedMessageCnt + secondLength>= this.getMaxMessageSize()) {
            throw new IOException("exceed max message length: " + this.getMaxMessageSize());
        }
        System.arraycopy(secoundBytes, 0, buffer, receivedMessageCnt, secondLength);
//        SystemLogger.info("byte length = {0}",getValidLength(buffer));
        receivedMessageCnt = receivedMessageCnt + secondLength;
        return receivedMessageCnt;
    }

    public int getValidLength(byte[] bytes){
        int i = 0;
        if (null == bytes || 0 == bytes.length)
            return i ;
        for (; i < bytes.length; i++) {
            if (bytes[i] == '\0')
                break;
        }
        return i + 1;
    }

    public boolean isAllZero(byte[] bytes){
        boolean bool = true;
        for (byte b : bytes) {
            if (b != 0) {
                bool = false;
                break;
            }
        }
        return bool;
    }

    /**
     * Writes the byte[] to the stream.
     */
    @Override
    public void serialize(byte[] bytes, OutputStream outputStream) throws IOException {
        SystemLogger.debugMethod(getClass(), "serialize", true, new String[] {});
        outputStream.write(bytes);
//        System.out.println(bytes.length);
        SystemLogger.debugMethod(getClass(), "serialize", false, new String[] {});
    }
}
