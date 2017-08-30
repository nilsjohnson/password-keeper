package passwordkeeper;

import accounttools.AccountTools;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class PasswordKeeper extends Application
{
	@Override
	public void start(Stage primaryStage)
	{
		// new root pane and login pane, to authenticate user
		Pane root = new Pane();
		LoginPane loginPane = new LoginPane();
		root.getChildren().add(loginPane);

		// scene and stage for the login screen
		Scene loginScene = new Scene(root, 300, 175);
		Stage loginStage = new Stage();

		// make response to resizing
		loginPane.layoutXProperty().bind(root.widthProperty().divide(2).subtract(loginPane.widthProperty().divide(2)));
		loginPane.layoutYProperty().bind(root.heightProperty().divide(2).subtract(loginPane.heightProperty().divide(2)));

		// launches login screen
		loginStage.setScene(loginScene);
		loginStage.setTitle("Login");
		loginStage.show();
		loginStage.requestFocus();

		// listens and closes login screen when user is authenticated.
		loginPane.getIsValidUser().addListener(new InvalidationListener()
		{
			@Override
			public void invalidated(Observable arg0)
			{
				loginStage.close();	
			}
		});

		if (!usersInFile())
		{
			loginPane.launchNewUserPane();
		}
	}

	private boolean usersInFile()
	{
		if (AccountTools.getNumberOfUsers() == 0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	public static void main(String[] args)
	{
		launch(args);
	}

}
