package passwordkeeper;

import java.io.Serializable;
import java.util.ArrayList;

import encryptionfilter.EncryptionFilter;
import javafx.collections.ObservableList;

// this is a horrible class name.
// this probably shouldnt even be a class.
public class EncryptedPwTransferObj implements Serializable 
{
	private transient String key;
	private ArrayList<Site> encryptedSites = new ArrayList<>();
	
	public EncryptedPwTransferObj(ObservableList<Site> rawSiteList, String key)
	{
		this.key = key;
		
		for(int i = 0; i < rawSiteList.size(); i++)
		{
			Site temp = new Site();
			temp.title = EncryptionFilter.encrypt(rawSiteList.get(i).title, key);
			temp.websiteUrl = EncryptionFilter.encrypt(rawSiteList.get(i).websiteUrl, key);
			temp.userName = EncryptionFilter.encrypt(rawSiteList.get(i).userName, key);
			temp.password = EncryptionFilter.encrypt(rawSiteList.get(i).password, key);
			temp.note = EncryptionFilter.encrypt(rawSiteList.get(i).note.toString(), key);
			encryptedSites.add(temp);
		}
	}
	
	public EncryptedPwTransferObj(String key)
	{
		this.key = key;
	}
	
	public ArrayList<Site> getList()
	{
		return encryptedSites;	
	}
	
	public String getKey()
	{
		return this.key;
	}
}
