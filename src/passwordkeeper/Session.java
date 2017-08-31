package passwordkeeper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Comparator;
import accounttools.AccountTools;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import encryptionfilter.EncryptionFilter;

public class Session
{
	private ObservableList<Site> observableSiteList = FXCollections.observableArrayList();
	private Stage primaryStage = new Stage();
	private LandingPane landingPane;
	private PwKeeperUser user = null;

	public Session(String userName, String pw) throws accounttools.BadLoginException
	{		
		// set the user object based on name/password, then load their sites
		user = new PwKeeperUser(AccountTools.getUserObject(userName, pw));
		user.setPwPlainText(pw);

		try
		{
			loadSitesFromUserFile();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		// sets action  events, listeners, and displays a LandingPane
		launchLandingPane();
	}

	private void loadSitesFromUserFile() throws IOException, ClassNotFoundException
	{
		ObservableList<Site> temp = FXCollections.observableArrayList();
		EncryptedPwTransferObj encryptedPwTransferObj = new EncryptedPwTransferObj(temp, user.getPwPlainText());
		{
			if (user.getPasswordFile().length() > 0)
			{
				try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(user.getPasswordFile().getPath()));)
				{
					// loads object properties
					encryptedPwTransferObj = (EncryptedPwTransferObj) input.readObject();
				}

				// decrypts and sets current session's  obsevableSiteList values
				for (int i = 0; i < encryptedPwTransferObj.getList().size(); i++)
				{
					Site curSite = new Site();
					curSite.title = EncryptionFilter.decrypt(encryptedPwTransferObj.getList().get(i).title, user.getPwPlainText());
					curSite.websiteUrl = EncryptionFilter.decrypt(encryptedPwTransferObj.getList().get(i).websiteUrl, user.getPwPlainText());
					curSite.userName = EncryptionFilter.decrypt(encryptedPwTransferObj.getList().get(i).userName, user.getPwPlainText());
					curSite.password = EncryptionFilter.decrypt(encryptedPwTransferObj.getList().get(i).password, user.getPwPlainText());
					curSite.note = EncryptionFilter.decrypt(encryptedPwTransferObj.getList().get(i).note, user.getPwPlainText());
					observableSiteList.add(curSite);
				}
			}
		}
	}

	private void launchLandingPane()
	{
		// step 1.) set up handlers
		class addSiteBtnHandler implements EventHandler<ActionEvent>
		{
			@Override
			public void handle(ActionEvent e)
			{
				launchAddSitePane();
			};
		}

		class searchHandler implements EventHandler<ActionEvent>
		{
			@Override
			public void handle(ActionEvent e)
			{
				System.out.println("search click!");
			}
		}

		class deleteHandler implements EventHandler<ActionEvent>
		{
			@Override
			public void handle(ActionEvent e)
			{
				observableSiteList.remove(landingPane.getSelectedSite());
			};
		}

		class saveHandler implements EventHandler<ActionEvent>
		{
			@Override
			public void handle(ActionEvent e)
			{
				System.out.println("save Clicked!");
				observableSiteList.get(landingPane.getSelectedSite()).title = landingPane.titleTf.getText();
				observableSiteList.get(landingPane.getSelectedSite()).websiteUrl = landingPane.urlTf.getText();
				observableSiteList.get(landingPane.getSelectedSite()).userName = landingPane.userNameTf.getText();
				observableSiteList.get(landingPane.getSelectedSite()).password = landingPane.passwordTf.getText();
				observableSiteList.get(landingPane.getSelectedSite()).note = landingPane.noteTa.getText();
				landingPane.saveChangesBtn.setDisable(true);
			};
		}
		
		// closing sequence
		primaryStage.setOnCloseRequest(e ->
		{
			try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(user.getPasswordFile().getPath()));)
			{
				output.writeObject(new EncryptedPwTransferObj(observableSiteList, user.getPwPlainText()));
			}
			catch (FileNotFoundException ex)
			{
				ex.printStackTrace();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		});
		
		class CloseProgramHandler implements EventHandler<ActionEvent>
		{

			@Override
			public void handle(ActionEvent e)
			{
				primaryStage.fireEvent(
                        new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST)
                );
			}
			
		}
		
		
		// makes LandingPane and puts it on a scene
		landingPane = new LandingPane(observableSiteList, new addSiteBtnHandler(), new searchHandler(), new saveHandler(), new deleteHandler(), new CloseProgramHandler());
		Scene scene = new Scene(landingPane, 520, 480);
		
		// add listeners
		landingPane.searchTf.textProperty().addListener(new ChangeListener<Object>()
		{
			@Override
			public void changed(ObservableValue<?> observable, Object oldValue, Object newValue)
			{
				selectBestMatch((String)newValue);
			}
		});
		
		// show LandingPane
		primaryStage.setScene(scene);
		primaryStage.setTitle("Password Keeper");
		primaryStage.show();
		primaryStage.requestFocus();
	}

	private void launchAddSitePane()
	{
		Stage newSiteStage = new Stage();
		AddSitePane addSitePane = new AddSitePane();

		class AddSiteHandler implements EventHandler<ActionEvent>
		{
			@Override
			public void handle(ActionEvent e)
			{
				addSite(addSitePane);
				sortSiteList();
				newSiteStage.close();

			};
		}

		class CancelHandler implements EventHandler<ActionEvent>
		{
			@Override
			public void handle(ActionEvent e)
			{
				System.out.println("Cancel Clicked");
				newSiteStage.close();
			};
		}

		addSitePane.setActionHandlers(new AddSiteHandler(), new CancelHandler());
		Scene newSiteScene = new Scene(addSitePane, 250, 275);
		
		
		newSiteStage.setScene(newSiteScene);
		newSiteStage.setTitle("Add Website");
		newSiteStage.show();
		newSiteStage.requestFocus();

	}

	private void addSite(AddSitePane addSitePane)
	{
		Site curSite = new Site();
		curSite.title = addSitePane.titleTf.getText();
		curSite.websiteUrl = addSitePane.urlTf.getText();
		curSite.userName = addSitePane.userNameTf.getText();
		curSite.password = addSitePane.passwordTf.getText();
		curSite.note = addSitePane.noteTa.getText();
		observableSiteList.add(curSite);
	}

	public void sortSiteList()
	{
		observableSiteList.sort(new Comparator<Site>()
		{
			@Override
			public int compare(Site one, Site other)
			{
				return one.title.toLowerCase().compareTo(other.title.toLowerCase());
			}
		});
	}
	
	private void selectBestMatch(String searchEntry)
	{
		int i = 0;
		String temp;
		while(i < observableSiteList.size())
		{
			temp = observableSiteList.get(i).title.toLowerCase();
			if (temp.startsWith(searchEntry.toLowerCase()))
			{
				landingPane.pwList.getSelectionModel().select(i);
				break;
			}
			else
			{
				i++;
			}
		}
	}
}
