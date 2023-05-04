package com.iaspec.uniongatewaymock.util;




/**
 * @author Flamenco.xxx
 * @date 2022/8/16  10:27
 */
public class CpicUtil {

//    需要配置或者修改
    private static int CM_CID_SIZE = 200;

    private static final int DATA_BUFFER_SIZE = 1024;

    private String systemDestName = "PIPE    ";








    public static byte[] convertToEbc(byte[] data, int size) {

        byte[] ebcByte = new byte[size];
        int i;
        for (i = 0; i < size; i++) {

            int asc = Byte.toUnsignedInt(data[i]);

            ebcByte[i] = (byte) asciiToEbcTable( asc);
        }
        return ebcByte;
    }


    public static int asciiToEbcTable(int ascii)
    {
        int ebcd;
        int tt[]= new int[]
                {
                        0x00,0x01,0x02,0x03,0x1A,0x09,0x1A,0x7F,        /*              */
                        0x1A,0x1A,0x1A,0x0B,0x0C,0x0D,0x0E,0x0F,        /*              */
                        0x10,0x11,0x12,0x13,0x3C,0x3D,0x32,0x26,        /*              */
                        0x18,0x19,0x3F,0x27,0x1C,0x1D,0x1E,0x1F,        /*              */
                        0x40,0x4F,0x7F,0x7B,0x5B,0x6C,0x50,0x7D,        /*  !"#$%&'     */
                        0x4D,0x5D,0x5C,0x4E,0x6B,0x60,0x4B,0x61,        /* ()*+,-./     */
                        0xF0,0xF1,0xF2,0xF3,0xF4,0xF5,0xF6,0xF7,        /* 01234567     */
                        0xF8,0xF9,0x7A,0x5E,0x4C,0x7E,0x6E,0x6F,        /* 89:;<=>?     */
                        0x7C,0xC1,0xC2,0xC3,0xC4,0xC5,0xC6,0xC7,        /* @ABCDEFG     */
                        0xC8,0xC9,0xD1,0xD2,0xD3,0xD4,0xD5,0xD6,        /* HIJKLMNO     */
                        0xD7,0xD8,0xD9,0xE2,0xE3,0xE4,0xE5,0xE6,        /* PQRSTUVW     */
                        0xE7,0xE8,0xE9,0x4A,0xE0,0x5A,0x5F,0x6D,        /* XYZ[\]^_     */
                        0x79,0x81,0x82,0x83,0x84,0x85,0x86,0x87,        /* `abcdefg     */
                        0x88,0x89,0x91,0x92,0x93,0x94,0x95,0x96,        /* hijklmno     */
                        0x97,0x98,0x99,0xA2,0xA3,0xA4,0xA5,0xA6,        /* pqrstuvw     */
                        0xA7,0xA8,0xA9,0xC0,0x6A,0xD0,0xA1,0x07,        /* xyz{|}~      */
                        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,        /*              */
                        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,        /*              */
                        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,        /*              */
                        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,        /*              */
                        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,        /*              */
                        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,        /*              */
                        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,        /*              */
                        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,        /*              */
                        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,        /*              */
                        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,        /*              */
                        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,        /*              */
                        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,        /*              */
                        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,        /*              */
                        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,        /*              */
                        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,        /*              */
                        0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,0x3F,        /*              */
                };

        ebcd=tt[ascii];
        return (ebcd);
    }



    public static byte[] convertToAsc(byte[] data, int size) {

        byte[] ascByte = new byte[size];
        int i;
        for (i = 0; i < size; i++) {

            int ebc = Byte.toUnsignedInt(data[i]);

            ascByte[i] = (byte)ebcToAsciiTable(ebc);
        }
        return ascByte;
    }






    public static int ebcToAsciiTable(int ebc)
    {
        int asc;
        int Tableebc2Asc[]= new int[]
                {
                        0x00,0x01,0x02,0x03,0x1A,0x09,0x1A,0x7F,
                        0x1A,0x1A,0x1A,0x0B,0x0C,0x0D,0x0E,0x0F,
                        0x10,0x11,0x12,0x13,0x1A,0x1A,0x08,0x1A,
                        0x18,0x19,0x1A,0x1A,0x1C,0x1D,0x1E,0x1F,
                        0x1A,0x1A,0x1A,0x1A,0x1A,0x0A,0x17,0x1B,
                        0x1A,0x1A,0x1A,0x1A,0x1A,0x05,0x06,0x07,
                        0x1A,0x1A,0x16,0x1A,0x1A,0x1A,0x1A,0x04,
                        0x1A,0x1A,0x1A,0x1A,0x14,0x15,0x1A,0x1A,
                        0x20,0x1A,0x1A,0x1A,0x1A,0x1A,0x1A,0x1A,
                        0x1A,0x1A,0x5B,0x2E,0x3C,0x28,0x2B,0x21,
                        0x26,0x1A,0x1A,0x1A,0x1A,0x1A,0x1A,0x1A,
                        0x1A,0x1A,0x5D,0x24,0x2A,0x29,0x3B,0x5E,
                        0x2D,0x2F,0x1A,0x1A,0x1A,0x1A,0x1A,0x1A,
                        0x1A,0x1A,0x7C,0x2C,0x25,0x5F,0x3E,0x3F,
                        0x1A,0x1A,0x1A,0x1A,0x1A,0x1A,0x1A,0x1A,
                        0x1A,0x60,0x3A,0x23,0x40,0x27,0x3D,0x22,
                        0x1A,0x61,0x62,0x63,0x64,0x65,0x66,0x67,
                        0x68,0x69,0x1A,0x1A,0x1A,0x1A,0x1A,0x1A,
                        0x1A,0x6A,0x6B,0x6C,0x6D,0x6E,0x6F,0x70,
                        0x71,0x72,0x1A,0x1A,0x1A,0x1A,0x1A,0x1A,
                        0x1A,0x7E,0x73,0x74,0x75,0x76,0x77,0x78,
                        0x79,0x7A,0x1A,0x1A,0x1A,0x1A,0x1A,0x1A,
                        0x1A,0x1A,0x1A,0x1A,0x1A,0x1A,0x1A,0x1A,
                        0x1A,0x1A,0x1A,0x1A,0x1A,0x1A,0x1A,0x1A,
                        0x7B,0x41,0x42,0x43,0x44,0x45,0x46,0x47,
                        0x48,0x49,0x1A,0x1A,0x1A,0x1A,0x1A,0x1A,
                        0x7D,0x4A,0x4B,0x4C,0x4D,0x4E,0x4F,0x50,
                        0x51,0x52,0x1A,0x1A,0x1A,0x1A,0x1A,0x1A,
                        0x5C,0x1A,0x53,0x54,0x55,0x56,0x57,0x58,
                        0x59,0x5A,0x1A,0x1A,0x1A,0x1A,0x1A,0x1A,
                        0x30,0x31,0x32,0x33,0x34,0x35,0x36,0x37,
                        0x38,0x39,0x1A,0x1A,0x1A,0x1A,0x1A,0x1A
                };

        asc=Tableebc2Asc[ebc];
        return (asc);
    }
}
