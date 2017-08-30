package passwordkeeper;

import accounttools.BadLoginException;
import accounttools.LoginError;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginPane extends VBox
{
	private Button loginBtn = new Button("Login");
	private Button newUserBtn = new Button("New User");
	private BooleanProperty isValidUser = new SimpleBooleanProperty(false);
	private Label userNameLbl = new Label("Username: ");
	private Label passwordLbl = new Label("Password: ");
	private PasswordField passwordPf = new PasswordField();
	private TextField userNameTf = new TextField();
	private GridPane entryGridPane = new GridPane();
	private HBox buttonHBox = new HBox();
	private Text messageTxt = new Text();

	public LoginPane()
	{

		buttonHBox.setPadding(new Insets(20, 30, 20, 50));

		entryGridPane.setVgap(5);
		entryGridPane.setHgap(5);
		entryGridPane.add(userNameLbl, 0, 0); // node, col, row
		entryGridPane.add(userNameTf, 1, 0);
		entryGridPane.add(passwordLbl, 0, 1);
		entryGridPane.add(passwordPf, 1, 1);

		buttonHBox.setSpacing(12);
		buttonHBox.getChildren().addAll(loginBtn, newUserBtn);

		getChildren().addAll(entryGridPane, messageTxt, buttonHBox);

		loginBtn.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				startNewSession(userNameTf.getText(), passwordPf.getText());
			}
		});
		newUserBtn.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				launchNewUserPane();

			}
		});
	}

	public BooleanProperty getIsValidUser()
	{
		return this.isValidUser;
	}

	public void launchNewUserPane()
	{
		Stage stage = new Stage();
		NewUserPane newUserPane = new NewUserPane();
		Scene scene = new Scene(newUserPane, 460, 185);
		stage.setScene(scene);
		stage.setTitle("Password Keeper");
		stage.setResizable(false);
		stage.show();
		stage.requestFocus();

		newUserPane.choseCancel.addListener(new ChangeListener<Object>()
		{
			@Override
			public void changed(ObservableValue<?> observable, Object oldValue, Object newValue)
			{
				stage.close();
			}
		});

		newUserPane.madeNewAccount.addListener(new ChangeListener<Object>()
		{
			@Override
			public void changed(ObservableValue<?> observable, Object oldValue, Object newValue)
			{
				startNewSession(newUserPane.nameTf.getText(), newUserPane.passwordPf.getText());
				stage.close();
			}
		});
	}

	public void startNewSession(String userName, String password)
	{
		try
		{
			new Session(userName, password);
			isValidUser.setValue(true);
			System.out.println("Login Success!");
		}
		catch (BadLoginException e)
		{
			if (e.getError() == LoginError.INVALID_PASSWORD)
			{
				passwordPf.setStyle("-fx-control-inner-background: red;");
				userNameTf.setStyle("-fx-control-inner-background: white;");
			}
			if (e.getError() == LoginError.USER_NOT_FOUND)
			{
				userNameTf.setStyle("-fx-control-inner-background: red;");
				passwordPf.setStyle("-fx-control-inner-background: white;");
			}
			if (e.getError() == LoginError.NO_USERS)
			{
				userNameTf.setStyle("-fx-control-inner-background: white;");
				passwordPf.setStyle("-fx-control-inner-background: white;");
			}
			messageTxt.setText(e.getLoginErrorMessage());
		}
	
	}
}
