package com.qingshuimonk.tdoaclient.utils;

import java.nio.ByteBuffer;

public class ByteArrayMethods{
		
		// merge two byte arrays
		public byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
			byte[] byte_3 = new byte[byte_1.length + byte_2.length];
			System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
			System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
			return byte_3;
		}

		// transform from double to byte
		public byte[] doubleToByte(double d) {
			byte[] b = new byte[8];
			long l = Double.doubleToLongBits(d);
			for (int i = 0; i < b.length; i++) {
				b[7 - i] = new Long(l).byteValue();
				l = l >> 8;
			}
			return b;
		}

		// transform from int to byte
		public byte[] intToByte(int n) {
			byte[] b = new byte[4];
			for (int i = 0; i < 4; i++) {
				b[i] = (byte) (n >> (24 - i * 8));
			}
			return b;
		}

		// transform from long to byte
		public byte[] longToByte(long n) {
			byte[] b = new byte[8];
			for (int i = 0; i < 8; i++) {
				b[i] = (byte) (n >> (56 - i * 8));
			}
			return b;
		}

		// transform from short to byte
		public byte[] shortToByte(short number) {
			byte[] b = new byte[2];
			for (int i = 1; i >= 0; i--) {
				b[i] = (byte) (number % 256);
				number >>= 8;
			}
			return b;
		}
		
		// transform from byte to long
		public long getLong(byte[] bytes) {
			ByteBuffer buffer = ByteBuffer.allocate(8);
			buffer.put(bytes, 0, bytes.length);
			buffer.flip();							// need flip
			return buffer.getLong();
		}

		public int byteToUShort(byte[] b) {
			int s = 0;
			short s0 = (short) (b[1] & 0xff);		// LSB
			short s1 = (short) (b[0] & 0xff);
			s1 <<= 8;
			s = (int) (s0 | s1);
			return s;
		}
}