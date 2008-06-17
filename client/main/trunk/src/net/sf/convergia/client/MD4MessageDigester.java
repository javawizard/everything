package net.sf.convergia.client;

/*
 * Created on Nov 29, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * An implementation of the MD4 Message Digest Algorithm. see RFC 1320
 * 
 * @author Alexander Boyd (alexander.boyd@trivergia.com)
 */
public class MD4MessageDigester
{

	public static void main(String[] args)
	{
		System.out.println(hexDigest(args[0].getBytes()));
	}

	public static String hexDigest(byte[] toDigest)
	{
		byte[] b = digest(toDigest);
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < b.length; i += 2)
		{
			int d = ((((int) b[i]) & 0x0FF) << 8) | (((int) b[i + 1]) & 0x0FF);
			buf.append(Integer.toHexString(d));
		}
		return buf.toString().toUpperCase();
	}

	public static byte[] digest(byte[] toDigest)
	{
		// refer to RFC 1320
		// convert bytes to bits and store as 0s and 1s
		byte[] bits = new byte[(toDigest.length * 8) + 1];
		for (int i = 0; i < (bits.length - 1); i += 8)
		{
			for (int j = 0; j < 8; j++)
			{
				int bitStart = toDigest[i / 8] >> j;
				bits[i + j] = (byte) (bitStart & 1);
			}
		}
		// add padding
		bits[bits.length - 1] = 1;
		byte[] newBits = null;
		for (int i = 0; i < 513; i++)
		{
			if (i == 512)
			{
				System.out.println("too much padding required");
				throw new RuntimeException("too much padding required");
			}
			if (((bits.length + i + 64) % 512) == 0)
			{
				newBits = new byte[bits.length + i + 64];
				break;
			}
		}
		for (int i = 0; i < bits.length; i++)
		{
			newBits[i] = bits[i];
		}
		// add length of message to end
		for (int i = 0; i < 64; i++)
		{
			newBits[i + (newBits.length - 64)] = (byte) ((bits.length >> i) & 1);
		}
		// initialize word array
		int[] M = new int[newBits.length / 32];
		for (int i = 0; i < newBits.length; i += 32)
		{
			for (int j = 0; j < 32; j++)
			{
				M[i / 32] = M[i / 32] | (newBits[i + j] << j);
			}
		}
		// initialize buffers
		int A = 0x01234567;
		int B = 0x89ABCDEF;
		int C = 0xFEDCBA98;
		int D = 0x76543210;
		// process message
		for (int i = 0; i < M.length; i += 16)
		{
			// copy current 512 bit block of M into X
			int[] X = new int[16];
			for (int j = 0; j < 16; j++)
			{
				X[j] = M[i + j];
			}
			// save old values
			int AA = A;
			int BB = B;
			int CC = C;
			int DD = D;
			// round 1
			int round1status = 0;
			for (int j = 0; j < 4; j++)
			{
				A = ROUND1(A, B, C, D, round1status++, 3, X);
				D = ROUND1(D, A, B, C, round1status++, 7, X);
				C = ROUND1(C, D, A, B, round1status++, 11, X);
				B = ROUND1(B, C, D, A, round1status++, 19, X);
			}
			// round 2
			A = ROUND2(A, B, C, D, 0, 3, X);
			D = ROUND2(D, A, B, C, 5, 5, X);
			C = ROUND2(C, D, A, B, 8, 9, X);
			B = ROUND2(B, C, D, A, 12, 13, X);
			A = ROUND2(A, B, C, D, 1, 3, X);
			D = ROUND2(D, A, B, C, 5, 5, X);
			C = ROUND2(C, D, A, B, 9, 9, X);
			B = ROUND2(B, C, D, A, 13, 13, X);
			A = ROUND2(A, B, C, D, 2, 3, X);
			D = ROUND2(D, A, B, C, 6, 5, X);
			C = ROUND2(C, D, A, B, 10, 9, X);
			B = ROUND2(B, C, D, A, 14, 13, X);
			A = ROUND2(A, B, C, D, 3, 3, X);
			D = ROUND2(D, A, B, C, 7, 5, X);
			C = ROUND2(C, D, A, B, 11, 9, X);
			B = ROUND2(B, C, D, A, 15, 13, X);
			// round 3
			A = ROUND3(A, B, C, D, 0, 3, X);
			D = ROUND3(D, A, B, C, 8, 9, X);
			C = ROUND3(C, D, A, B, 4, 11, X);
			B = ROUND3(B, C, D, A, 12, 15, X);
			A = ROUND3(A, B, C, D, 2, 3, X);
			D = ROUND3(D, A, B, C, 10, 9, X);
			C = ROUND3(C, D, A, B, 6, 11, X);
			B = ROUND3(B, C, D, A, 14, 15, X);
			A = ROUND3(A, B, C, D, 1, 3, X);
			D = ROUND3(D, A, B, C, 9, 9, X);
			C = ROUND3(C, D, A, B, 5, 11, X);
			B = ROUND3(B, C, D, A, 13, 15, X);
			A = ROUND3(A, B, C, D, 3, 3, X);
			D = ROUND3(D, A, B, C, 11, 9, X);
			C = ROUND3(C, D, A, B, 7, 11, X);
			B = ROUND3(B, C, D, A, 15, 15, X);
			// save info & old values
			A = A + AA;
			B = B + BB;
			C = C + CC;
			D = D + DD;
		}
		// create array to return
		byte[] toReturn = new byte[16];
		toReturn[0] = (byte) (A & 0xFF);
		toReturn[1] = (byte) ((A & 0xFF00) >> 8);
		toReturn[2] = (byte) ((A & 0xFF0000) >> 16);
		toReturn[3] = (byte) ((A & 0xFF000000) >> 24);
		toReturn[4] = (byte) (B & 0xFF);
		toReturn[5] = (byte) ((B & 0xFF00) >> 8);
		toReturn[6] = (byte) ((B & 0xFF0000) >> 16);
		toReturn[7] = (byte) ((B & 0xFF000000) >> 24);
		toReturn[8] = (byte) (C & 0xFF);
		toReturn[9] = (byte) ((C & 0xFF00) >> 8);
		toReturn[10] = (byte) ((C & 0xFF0000) >> 16);
		toReturn[11] = (byte) ((C & 0xFF000000) >> 24);
		toReturn[12] = (byte) (D & 0xFF);
		toReturn[13] = (byte) ((D & 0xFF00) >> 8);
		toReturn[14] = (byte) ((D & 0xFF0000) >> 16);
		toReturn[15] = (byte) ((D & 0xFF000000) >> 24);
		return toReturn;
	}

	/**
	 * If X then Y else Z function
	 * 
	 * @param X
	 * @param Y
	 * @param Z
	 * @return
	 */
	private static int F(int X, int Y, int Z)
	{
		return (X & Y) | (not(X) & Z);
	}

	/**
	 * Majority function
	 * 
	 * @param X
	 * @param Y
	 * @param Z
	 * @return
	 */
	private static int G(int X, int Y, int Z)
	{
		return (X & Y) | (X & Z) | (Y & Z);
	}

	/**
	 * XOR function
	 * 
	 * @param X
	 * @param Y
	 * @param Z
	 * @return
	 */
	private static int H(int X, int Y, int Z)
	{
		return X ^ Y ^ Z;
	}

	private static int not(int X)
	{
		return X ^ 0xFFFFFFFF;
	}

	private static int ROUND1(int A, int B, int C, int D, int K, int S, int[] X)
	{
		return circularLeftShift(ROUND1S(A, B, C, D, K, S, X), S);
	}

	private static int ROUND2(int A, int B, int C, int D, int K, int S, int[] X)
	{
		return circularLeftShift(ROUND2S(A, B, C, D, K, S, X), S);
	}

	private static int ROUND3(int A, int B, int C, int D, int K, int S, int[] X)
	{
		return circularLeftShift(ROUND3S(A, B, C, D, K, S, X), S);
	}

	private static int ROUND1S(int A, int B, int C, int D, int K, int S, int[] X)
	{
		return A + F(B, C, D) + X[K];
	}

	private static int ROUND2S(int A, int B, int C, int D, int K, int S, int[] X)
	{
		return A + G(B, C, D) + X[K] + 0x5A827999;
	}

	private static int ROUND3S(int A, int B, int C, int D, int K, int S, int[] X)
	{
		return A + H(B, C, D) + X[K] + 0x6ED9EBA1;
	}

	/**
	 * rotate X left by S bits.
	 * 
	 * @param X
	 * @param S
	 * @return
	 */
	private static int circularLeftShift(int X, int S)
	{
		for (int i = 0; i < S; i++)
		{
			X = (X << 1) | (((X & 0x80000000) != 0) ? 1 : 0);
		}
		return X;
	}
}