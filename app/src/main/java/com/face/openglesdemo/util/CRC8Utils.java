package com.face.openglesdemo.util;

public class CRC8Utils {

        /**
         * CRC-8
         *
         * @param  source
         * @param  offset
         * @param  length
         * @return
         */
        public static int CRC8(byte[] source, int offset, int length) {
            int wCRCin = 0x00;
            int wCPoly = 0x07;
            for (int i = offset, cnt = offset + length; i < cnt; i++) {
                for (int j = 0; j < 8; j++) {
                    boolean bit = ((source[i] >> (7 - j) & 1) == 1);
                    boolean c07 = ((wCRCin >> 7 & 1) == 1);
                    wCRCin <<= 1;
                    if (c07 ^ bit)
                        wCRCin ^= wCPoly;
                }
            }
            wCRCin &= 0xFF;
            return wCRCin ^= 0x00;
        }

        /**
         * CRC-8/DARC
         *
         * @param  source
         * @param  offset
         * @param  length
         * @return
         */
        public static int CRC8_DARC(byte[] source, int offset, int length) {
            int wCRCin = 0x00;
            // Integer.reverse(0x39) >>> 24
            int wCPoly = 0x9C;
            for (int i = offset, cnt = offset + length; i < cnt; i++) {
                wCRCin ^= ((long) source[i] & 0xFF);
                for (int j = 0; j < 8; j++) {
                    if ((wCRCin & 0x01) != 0) {
                        wCRCin >>= 1;
                        wCRCin ^= wCPoly;
                    } else {
                        wCRCin >>= 1;
                    }
                }
            }
            return wCRCin ^= 0x00;
        }

        /**
         * CRC-8/ITU
         *
         * @param  source
         * @param  offset
         * @param  length
         * @return
         */
        public static int CRC8_ITU(byte[] source, int offset, int length) {
            int wCRCin = 0x00;
            int wCPoly = 0x07;
            for (int i = offset, cnt = offset + length; i < cnt; i++) {
                for (int j = 0; j < 8; j++) {
                    boolean bit = ((source[i] >> (7 - j) & 1) == 1);
                    boolean c07 = ((wCRCin >> 7 & 1) == 1);
                    wCRCin <<= 1;
                    if (c07 ^ bit)
                        wCRCin ^= wCPoly;
                }
            }
            wCRCin &= 0xFF;
            return wCRCin ^= 0x55;
        }

        /**
         * CRC-8/MAXIM
         *
         * @param  source   10进制byte数组
         * @param  offset   开始值
         * @param  length   数组长度
         * @return
         */
        public static int CRC8_MAXIM(byte[] source, int offset, int length) {
            int wCRCin = 0x00;
            // Integer.reverse(0x31) >>> 24
            int wCPoly = 0x8C;
            for (int i = offset, cnt = offset + length; i < cnt; i++) {
                wCRCin ^= ((long) source[i] & 0xFF);
                for (int j = 0; j < 8; j++) {
                    if ((wCRCin & 0x01) != 0) {
                        wCRCin >>= 1;
                        wCRCin ^= wCPoly;
                    } else {
                        wCRCin >>= 1;
                    }
                }
            }
            return wCRCin;
        }

        /**
         * CRC-8/ROHC
         *
         * @param  source
         * @param  offset
         * @param  length
         * @return
         */
        public static int CRC8_ROHC(byte[] source, int offset, int length) {
            int wCRCin = 0xFF;
            // Integer.reverse(0x07) >>> 24
            int wCPoly = 0xE0;
            for (int i = offset, cnt = offset + length; i < cnt; i++) {
                wCRCin ^= ((long) source[i] & 0xFF);
                for (int j = 0; j < 8; j++) {
                    if ((wCRCin & 0x01) != 0) {
                        wCRCin >>= 1;
                        wCRCin ^= wCPoly;
                    } else {
                        wCRCin >>= 1;
                    }
                }
            }
            return wCRCin ^= 0x00;
        }

}
