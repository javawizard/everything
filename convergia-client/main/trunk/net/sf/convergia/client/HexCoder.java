package net.sf.convergia.client;

public class HexCoder
{
	public static String encode(String input)
	{
		StringBuilder output = new StringBuilder();
		for (char c : input.toCharArray())
		{
			output.append(pad(Integer.toHexString(c & 0xFF)));
		}
		return output.toString();
	}

	public static String decode(String input)
	{
		StringBuilder output = new StringBuilder();
		{
			for (int i = 0; i < input.length(); i += 2)
			{
				output.append((char) Integer.parseInt("" + input.charAt(i) + ""
						+ input.charAt(i + 1), 16));
			}
		}
		return output.toString();
	}

	public static void main(String[] args)

	{
		String encoded = encode("Hello! This test was successful!");
		System.out.println(encoded);
		String decoded = decode(encoded);
		System.out.println(decoded);
	}

	private static String pad(String string)
	{
		while (string.length() < 2)
			string = "0" + string;
		return string;
	}
}
