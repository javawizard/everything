package net.sf.convergia.client;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class A7Checksum
{
	public static final int POSITIVE_LENGTH = 11;

	public static final int NEGATIVE_LENGTH = 8;

	public static String checksum(InputStream input)
	{
		return checksum(input, POSITIVE_LENGTH, NEGATIVE_LENGTH);
	}

	private static String checksum(InputStream fis2,
			int positiveChecksumLength, int negativeChecksumLength)
			throws IOException
	{
		try
		{
			BufferedInputStream fis = new BufferedInputStream(fis2, 4096);
			byte[] positiveChecksum = new byte[positiveChecksumLength];
			byte[] negativeChecksum = new byte[negativeChecksumLength];
			int currentChecksumIndex = 0;
			int i;
			while ((i = fis.read()) != -1)
			{
				positiveChecksum[currentChecksumIndex % positiveChecksum.length] ^= i;
				negativeChecksum[currentChecksumIndex % negativeChecksum.length] ^= (i ^ 0xFF);
				currentChecksumIndex++;
			}
			String checksum = "" + currentChecksumIndex + "x";
			for (i = 0; i < positiveChecksum.length; i++)
			{
				checksum += Integer.toHexString(positiveChecksum[i] + 128);
			}
			checksum += "x";
			for (i = 0; i < negativeChecksum.length; i++)
			{
				checksum += Integer.toHexString(negativeChecksum[i] + 128);
			}
			fis.close();
			fis2.close();
			return checksum;
		} catch (IOException e)
		{
			e.printStackTrace();
			throw e;
		}
	}
}
