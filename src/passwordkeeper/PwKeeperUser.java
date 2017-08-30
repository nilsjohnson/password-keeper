package passwordkeeper;

import java.io.File;

import accounttools.User;

public class PwKeeperUser extends User
{
	private File passwordFile;
	
	
	public PwKeeperUser(User u)
	{
		super(u.getUsername(), u.getPwCipherText());
		setPasswordFile();
	}
	
	private void setPasswordFile()
	{
		passwordFile = new File("keep/" + userName + ".dat");
	}

	public File getPasswordFile()
	{
		return this.passwordFile;
	}
}
