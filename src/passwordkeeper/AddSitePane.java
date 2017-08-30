package passwordkeeper;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class AddSitePane extends VBox
{
	public GridPane entryPane = new GridPane();
	private HBox buttonBox = new HBox();
	private Button addSiteBtn = new Button("Add Website");
	private Button cancelBtn = new Button("Cancel");
	public SimpleBooleanProperty choseCancel = new SimpleBooleanProperty(false);
	private Label titleLbl = new Label("Entry Title: ");
	private Label urlLbl = new Label("URL: ");
	private Label userNameLbl = new Label("User Name: ");
	private Label passwordLbl = new Label("Password: ");
	private Label noteLbl = new Label("Notes: ");
	public TextField titleTf = new TextField();
	public TextField urlTf = new TextField();
	public TextField userNameTf = new TextField();
	public TextField passwordTf = new TextField();
	public TextArea noteTa = new TextArea();

	public AddSitePane()
	{
		entryPane.setVgap(5);
		entryPane.setHgap(5);
		entryPane.add(titleLbl, 0, 0);
		entryPane.add(titleTf, 1, 0);
		entryPane.add(urlLbl, 0, 1);
		entryPane.add(urlTf, 1, 1);
		entryPane.add(userNameLbl, 0, 2);
		entryPane.add(userNameTf, 1, 2);
		entryPane.add(passwordLbl, 0, 3);
		entryPane.add(passwordTf, 1, 3);

		buttonBox.getChildren().add(addSiteBtn);
		buttonBox.getChildren().add(cancelBtn);

		setPadding(new Insets(20, 15, 20, 15));
		buttonBox.setSpacing(12);
		buttonBox.setPadding(new Insets(5, 5, 5, 5));
		
		noteTa.prefWidthProperty().bind(this.widthProperty().subtract(10));
		
		getChildren().addAll(entryPane, noteLbl, noteTa, buttonBox);
	}
	public void setActionHandlers(EventHandler<ActionEvent> addSiteHandler, EventHandler<ActionEvent> cancelHandler)
	{
		addSiteBtn.setOnAction(addSiteHandler);
		cancelBtn.setOnAction(cancelHandler);

	}
}
