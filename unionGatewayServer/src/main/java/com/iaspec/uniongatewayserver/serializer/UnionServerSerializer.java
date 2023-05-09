package com.iaspec.uniongatewayserver.serializer;


import com.iaspec.uniongatewayserver.util.SystemLogger;
import org.springframework.integration.ip.tcp.serializer.AbstractPooledBufferByteArraySerializer;
import org.springframework.integration.ip.tcp.serializer.SoftEndOfStreamException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author Flamenco.xxx
 * @date 2022/8/15  14:38
 */
public class UnionServerSerializer extends AbstractPooledBufferByteArraySerializer {


    public UnionServerSerializer() {
        super();
    }

    public UnionServerSerializer(int maxMessageSize){
        super();
        this.setMaxMessageSize(maxMessageSize);
    }

    @Override
    protected byte[] doDeserialize(InputStream inputStream, byte[] buffer) throws IOException{
//        SystemLogger.debugMethod(getClass(), "doDeserialize", true, new String[] {});
        byte[] bytes;
        try {
            int deserializedMessageLength = this.getBytesLen(inputStream, buffer);
            if (deserializedMessageLength == 0) {
                // empty message, means the message is drop
                bytes = new byte[0];
            } else {
                bytes = Arrays.copyOfRange(buffer, 4, deserializedMessageLength);
            }
            return bytes;
        } catch (Exception e) {
            SystemLogger.error("Occurs an Error while deserializing message, e.message={0}", new String[] { e.getMessage() }, e);
            throw e;
        } finally {

//            SystemLogger.debugMethod(getClass(), "doDeserialize", false, new String[] { "bytes.length" }, bytes.length);
        }
    }

    @Override
    public void serialize(byte[] bytes, OutputStream outputStream) throws IOException { //        SystemLogger.infoMethod(getClass(), "serialize", true, new String[] {"bytes.length"}, bytes.length);
        try {
            SystemLogger.debug("bytes.length={0}", bytes.length);
            byte[] totalBytes = new byte[4 + bytes.length];
            byte[] lengthBytes = getMessageLength(bytes).getBytes(StandardCharsets.US_ASCII);
            SystemLogger.debug("lengthBytes.length={0}", lengthBytes.length);
            System.arraycopy(lengthBytes, 0, totalBytes, 0, lengthBytes.length);
            System.arraycopy(bytes, 0, totalBytes, lengthBytes.length, bytes.length);
            outputStream.write(totalBytes);
        } catch (Exception e) {
            SystemLogger.error("Occurs an Error while serializing message, e.message={0}", new String[] { e.getMessage() }, e);
            throw e;
        } finally {
//            SystemLogger.infoMethod(getClass(), "serialize", false, new String[] {});
        }
    }


    public synchronized int getBytesLen(InputStream inputStream,byte[] buffer) throws IOException {
//        SystemLogger.debugMethod(getClass(), "getBytes", true, new String[] {});

        try{
            inputStream.read(buffer, 0, 4);

            byte[] theFirstFourBytes = Arrays.copyOfRange(buffer, 0, 4);

            if (isAllZero(theFirstFourBytes)){
                return 0;
            }
            int targetMessageLength = Integer.parseInt(new String(theFirstFourBytes, StandardCharsets.US_ASCII));

            int realMessageLength = checkIntegrityAndPopulateBuffer(inputStream,targetMessageLength,4,buffer);
            if (targetMessageLength != realMessageLength - 4) {
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

    public int getBytesLength(InputStream inputStream, byte[] buffer) throws IOException {
        SystemLogger.debugMethod(getClass(), "getBytes", true, new String[] {});
        int receivedMessageCnt = 0;
        try {

            while (receivedMessageCnt < 4) {

                int receivedMessageSubCnt = inputStream.read(buffer, receivedMessageCnt, 4 - receivedMessageCnt);

                if (receivedMessageSubCnt < 0) {
                    receivedMessageCnt = receivedMessageSubCnt;
                    break;
                } else {
                    receivedMessageCnt = receivedMessageCnt + receivedMessageSubCnt;
                    SystemLogger.debug("buffer={0}", new String(buffer));
                    SystemLogger.debug("receivedMessageCnt={0}", receivedMessageCnt);
                }
            }

            if (receivedMessageCnt < 0) {
                throw new SoftEndOfStreamException("Stream closed between payloads");
            }

            if (receivedMessageCnt >= this.getMaxMessageSize()) {
                throw new IOException("exceed max message length: " + this.getMaxMessageSize());
            }

            byte[] theFirstFourBytes = Arrays.copyOfRange(buffer, 0, 4);
            int targetMessageLength = Integer.valueOf(new String(theFirstFourBytes));

            return checkIntegrityAndPopulateBuffer(inputStream, targetMessageLength, receivedMessageCnt, buffer);

        } catch (SoftEndOfStreamException e) {
            throw e;
        } catch (IOException e) {
            publishEvent(e, buffer, receivedMessageCnt);
            throw e;
        } catch (RuntimeException e) {
            publishEvent(e, buffer, receivedMessageCnt);
            throw e;
        } finally {
            SystemLogger.debugMethod(getClass(), "getBytes", false, new String[] {});
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

    private int countinusToGetBytes(InputStream inputStream, int receivedMessageCnt, byte[] buffer, byte[] secoundBytes)
            throws IOException {
        int secondLength = inputStream.read(secoundBytes);
        if (receivedMessageCnt + secondLength>= this.getMaxMessageSize()) {
            throw new IOException("exceed max message length: " + this.getMaxMessageSize());
        }
        SystemLogger.debug("buffer.length ={0}, receivedMessageCnt={1}, secondLength={2}, secoundBytes.length={3}",
                buffer.length, receivedMessageCnt, secondLength, secoundBytes.length);
        System.arraycopy(secoundBytes, 0, buffer, receivedMessageCnt, secondLength);
        receivedMessageCnt = receivedMessageCnt + secondLength;
        return receivedMessageCnt;
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


    private String getMessageLength(byte[] bytes) {

        String messageLength = String.valueOf(bytes.length);
//        SystemLogger.debug("messageLength={0}", messageLength);
        if (messageLength.length() < 4) {
            StringBuilder sb = new StringBuilder();
            int position = 4 - messageLength.length();
            for (int i = 0; i < position; i++) {
                sb.append("0");
            }
            sb.append(messageLength);
            return sb.toString();
        } else {
            return messageLength;
        }
    }

}
