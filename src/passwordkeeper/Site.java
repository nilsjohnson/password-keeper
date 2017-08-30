package passwordkeeper;

import java.io.Serializable;

public class Site implements Serializable
{
	public String title;
	public String websiteUrl;
	public String userName;
	public String password;
	public String note;
	
	@Override
	public String toString()
	{
		return title;

	}
}
