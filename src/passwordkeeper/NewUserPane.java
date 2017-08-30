package passwordkeeper;

import java.io.IOException;
import java.util.ArrayList;

import accounttools.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class NewUserPane extends HBox
{
	private GridPane entryPane = new GridPane();
	private HBox buttonHBox = new HBox();
	private VBox entryContainerVBox = new VBox();
	private Button makeUserBtn = new Button("Create Account");
	private Button cancelBtn = new Button("Cancel");
	public SimpleBooleanProperty choseCancel = new SimpleBooleanProperty(false);
	public SimpleBooleanProperty madeNewAccount = new SimpleBooleanProperty(false);

	private Label usernameLbl = new Label("Desired Username: ");
	private Label passwordLbl = new Label("Password: ");
	private Label pwConfirmLbl = new Label("Confirm: ");
	public TextField nameTf = new TextField();
	public TextField passwordPf = new PasswordField();
	public TextField passwordConfirmPf = new PasswordField();
	private Text messageTxt = new Text();
	private StackPane messageTxtPane = new StackPane();
	
	// these fields are all for giving feedback as user enters desired username/pw
	private VBox requirementVBox = new VBox();
	private Label reqLbl = new Label("");
	private Text maxLengthTxt = new Text();
	private Text minLengthTxt = new Text();
	private Text upperTxt = new Text("Uppercase Character(s)");
	private Text lowerTxt = new Text("Lowercase Character(s)");
	private Text numberTxt = new Text("Number(s)");
	private Text specialTxt = new Text();
	private Text noIllegalCharTxt = new Text();
	private Text availableStatusTxt = new Text();
	private Text matchTxt = new Text("To Be Matching");
	
	private boolean isSetForNameEntry;
	private boolean isSetForPwEntry;

	public NewUserPane()
	{
		entryPane.setVgap(5);
		entryPane.setHgap(5);

		entryPane.add(usernameLbl, 0, 0);
		entryPane.add(nameTf, 1, 0);
		entryPane.add(passwordLbl, 0, 1);
		entryPane.add(passwordPf, 1, 1);
		entryPane.add(pwConfirmLbl, 0, 2);
		entryPane.add(passwordConfirmPf, 1, 2);

		buttonHBox.getChildren().add(makeUserBtn);
		buttonHBox.getChildren().add(cancelBtn);

		setPadding(new Insets(20, 15, 20, 15));
		buttonHBox.setSpacing(12);
		buttonHBox.setPadding(new Insets(20, 30, 20, 20));

		entryContainerVBox.getChildren().addAll(entryPane, buttonHBox);
		this.getChildren().add(entryContainerVBox);

		messageTxtPane.setPadding(new Insets(5, 5, 5, 10));

		setForNameEntry();
		requirementVBox.setPadding(new Insets(5, 5, 5, 5));

		makeUserBtn.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				try
				{
					AccountTools.makeNewAccount(nameTf.getText(), passwordPf.getText(), passwordConfirmPf.getText());
					madeNewAccount.set(true);
					System.out.println("New User Created!!");
				}
				catch (NewUserException e)
				{
					if (e instanceof BadPasswordException)
					{
						passwordPf.setStyle("-fx-control-inner-background: red;");
						passwordConfirmPf.setStyle("-fx-control-inner-background: red;");
						messageTxt.setText("Bad Password");
					}
					if (e instanceof BadUsernameException)
					{
						nameTf.setStyle("-fx-control-inner-background: red;");
						messageTxt.setText("Bad Name");
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}

			}
		});
		
		class PwChangeListener implements ChangeListener
		{
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue)
			{
				if (isSetForNameEntry)
				{
					setForPwEntry();
				}

				passwordPf.setStyle("-fx-control-inner-background: white;");
				passwordConfirmPf.setStyle("-fx-control-inner-background: white;");

				ArrayList<PasswordError> errorList = AccountTools.checkPasswordLegality(passwordPf.getText());
				updatePwStatus(errorList);
			}
		}
		
		class usernameChangeListener implements ChangeListener
		{
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue)
			{
				// minLengthTxt, specialTxt, maxLengthTxt
				if (isSetForPwEntry)
				{
					setForNameEntry();
				}
				
				nameTf.setStyle("-fx-control-inner-background: white;");

				ArrayList<UsernameError> errorList = AccountTools.checkUsernameLegality(nameTf.getText());
				updateNameStatus(errorList);
			}
		}
		
		
		passwordPf.textProperty().addListener(new PwChangeListener());
		passwordPf.focusedProperty().addListener(new PwChangeListener());

		nameTf.textProperty().addListener(new usernameChangeListener());
		nameTf.focusedProperty().addListener(new usernameChangeListener());
		
		passwordConfirmPf.textProperty().addListener(new ChangeListener<Object>()
		{
			@Override
			public void changed(ObservableValue<?> observable, Object oldValue, Object newValue)
			{
				// minLengthTxt, specialTxt, maxLengthTxt
				if (isSetForNameEntry)
				{
					setForPwEntry();
				}

				ArrayList<PasswordError> errorList = AccountTools.checkPasswordLegality(passwordPf.getText());
				updatePwStatus(errorList);;
			}
		});

		cancelBtn.setOnAction(e -> choseCancel.set(true));
		requestFocus();
	}

	private void setForNameEntry()
	{
		if (this.getChildren().contains(requirementVBox))
		{
			this.getChildren().remove(requirementVBox);
			requirementVBox.getChildren().removeAll(reqLbl, minLengthTxt, upperTxt, lowerTxt, numberTxt, specialTxt, matchTxt, noIllegalCharTxt, maxLengthTxt);
		}
		reqLbl.setText("Usernames Need:");
		minLengthTxt.setText("At Least " + AccountTools.MIN_NAME_LENGTH + " Characters");
		noIllegalCharTxt.setText("");
		maxLengthTxt.setText("");
		requirementVBox.getChildren().addAll(reqLbl, minLengthTxt, availableStatusTxt, noIllegalCharTxt, maxLengthTxt);
		this.getChildren().add(requirementVBox);
		isSetForNameEntry = true;
		isSetForPwEntry = false;
	}

	private void setForPwEntry()
	{
		this.getChildren().remove(requirementVBox);
		requirementVBox.getChildren().removeAll(reqLbl, minLengthTxt, availableStatusTxt, noIllegalCharTxt, maxLengthTxt);
		reqLbl.setText("Passwords Need:");
		minLengthTxt.setText("At Least " + AccountTools.MIN_PW_LENGTH + " Characters");
		specialTxt.setText("Special Characters(s)");
		maxLengthTxt.setText("");
		requirementVBox.getChildren().addAll(reqLbl, minLengthTxt, upperTxt, lowerTxt, numberTxt, specialTxt, matchTxt, noIllegalCharTxt, maxLengthTxt);
		this.getChildren().add(requirementVBox);
		isSetForNameEntry = false;
		isSetForPwEntry = true;
	}

	private void updatePwStatus(ArrayList<PasswordError> errorList)
	{
		if (errorList != null && errorList.contains(PasswordError.TOO_SHORT))
		{
			minLengthTxt.setFill(Color.BLACK);
		}
		else
		{
			minLengthTxt.setFill(Color.GREEN);
		}

		if (errorList != null && errorList.contains(PasswordError.NEEDS_UPPER))
		{
			upperTxt.setFill(Color.BLACK);
		}
		else
		{
			upperTxt.setFill(Color.GREEN);
		}

		if (errorList != null && errorList.contains(PasswordError.NEEDS_LOWER))
		{
			lowerTxt.setFill(Color.BLACK);
		}
		else
		{
			lowerTxt.setFill(Color.GREEN);
		}

		if (errorList != null && errorList.contains(PasswordError.NEEDS_NUMBER))
		{
			numberTxt.setFill(Color.BLACK);
		}
		else
		{
			numberTxt.setFill(Color.GREEN);
		}

		if (errorList != null && errorList.contains(PasswordError.NEEDS_SPECIAL))
		{
			specialTxt.setFill(Color.BLACK);
		}
		else
		{
			specialTxt.setFill(Color.GREEN);
		}

		if (errorList != null && errorList.contains(PasswordError.HAS_ILLEGAL_CHAR))
		{
			noIllegalCharTxt.setText("Allowed Special Characters: \n\t" + getLegalPwSpecialChar());
			noIllegalCharTxt.setFill(Color.BLACK);
			
		}
		else
		{
			noIllegalCharTxt.setText("");
		}
		
		if (errorList != null && errorList.contains(PasswordError.TOO_LONG))
		{
			maxLengthTxt.setFill(Color.BLACK);
			maxLengthTxt.setText("No More Than " + AccountTools.MAX_PW_LENGTH + " Characters");
		}
		else
		{
			maxLengthTxt.setText("");
		}
		
		if(!passwordPf.getText().equals(passwordConfirmPf.getText()) )
		{
			matchTxt.setFill(Color.BLACK);
		}
		else if (passwordPf.getLength() > 1)
		{
			matchTxt.setFill(Color.GREEN);
		}
	}

	private void updateNameStatus(ArrayList<UsernameError> errorList)
	{
		if (errorList != null && errorList.contains(UsernameError.TOO_SHORT))
		{
			minLengthTxt.setFill(Color.BLACK);
		}
		else
		{
			minLengthTxt.setFill(Color.GREEN);
		}

		if (errorList != null && errorList.contains(UsernameError.HAS_ILLEGAL_CHAR))
		{
			noIllegalCharTxt.setText("No Illegal Characters: \n\t" + getIllegalNameChar());
			noIllegalCharTxt.setFill(Color.BLACK);
		}
		else
		{
			noIllegalCharTxt.setText("");
		}
		if (errorList != null && errorList.contains(UsernameError.TOO_LONG))
		{
			maxLengthTxt.setText("No More Than " + AccountTools.MAX_NAME_LENGTH + " Characters");
			maxLengthTxt.setFill(Color.BLACK);
		}
		else
		{
			maxLengthTxt.setText("");
		}
		
		if (errorList == null && !AccountTools.isAvailable(nameTf.getText()))
		{
			availableStatusTxt.setText("Username Taken, Sorry!");
		}
		else
		{
			availableStatusTxt.setText("");
		}
	}
	
	private String getLegalPwSpecialChar()
	{
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < AccountTools.LEGAL_PW_SPECIAL_CHARACTER.length; i++)
		{
			if (i < AccountTools.LEGAL_PW_SPECIAL_CHARACTER.length - 1)
			{
				str.append(AccountTools.LEGAL_PW_SPECIAL_CHARACTER[i] + ", ");
			}
			else
			{
				str.append(AccountTools.LEGAL_PW_SPECIAL_CHARACTER[i]);
			}
		}
		return str.toString();
	}

	private String getIllegalNameChar()
	{
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < AccountTools.ILLEGAL_NAME_CHARACTER.length; i++)
		{
			if (i < AccountTools.ILLEGAL_NAME_CHARACTER.length - 1)
			{
				str.append(AccountTools.ILLEGAL_NAME_CHARACTER[i] + ", ");
			}
			else
			{
				str.append(AccountTools.ILLEGAL_NAME_CHARACTER[i]);
			}
		}
		return str.toString();
	}
}
