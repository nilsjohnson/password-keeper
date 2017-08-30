package passwordkeeper;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LandingPane extends VBox
{
	public ListView<Site> pwList = null;

	private VBox pwListBox = new VBox();
	public HBox mainViewHBox = new HBox();
	public HBox searchBox = new HBox();
	public VBox pwViewAndEditVBox = new VBox();
	public GridPane pwViewGridPane = new GridPane();
	public HBox pwEditButtonHBox = new HBox();

	private Button searchBtn = new Button("Search");
	private Button newPwBtn = new Button("New Entry");
	public Button saveChangesBtn = new Button("Save Changes");
	private Button deleteBtn = new Button("Delete");

	private Label titleLbl = new Label("Website: ");
	private Label urlLbl = new Label("URL: ");
	private Label userNameLbl = new Label("User Name: ");
	private Label passwordLbl = new Label("Password: ");
	private Label noteLbl = new Label("Notes: ");

	public TextField searchTf = new TextField();
	public TextField titleTf = new TextField();
	public TextField urlTf = new TextField();
	public TextField userNameTf = new TextField();
	public TextField passwordTf = new TextField();
	public TextArea noteTa = new TextArea();

	public MenuBar menuBar = new MenuBar();
	private Menu fileMenu = new Menu("File");
	private MenuItem newEntryMenuItem = new MenuItem("New Entry");
	private MenuItem exitMenuItem = new MenuItem("Exit");

	private int selectedSite;

	public LandingPane(ObservableList<Site> obserableSiteList, EventHandler<ActionEvent> addSiteHandler, EventHandler<ActionEvent> searchHandler, EventHandler<ActionEvent> saveHandler, EventHandler<ActionEvent> deleteHandler, EventHandler<ActionEvent> closeProgramHandler)
	{
		// gives pane access to site list of the Session object
		pwList = new ListView<>(obserableSiteList);

		// set menu actions
		newEntryMenuItem.setOnAction(addSiteHandler);
		exitMenuItem.setOnAction(closeProgramHandler);

		// add menu items to menu and
		fileMenu.getItems().addAll(newEntryMenuItem, exitMenuItem);
		menuBar.getMenus().add(fileMenu);

		// set up pwListBox
		pwListBox.setSpacing(10);
		pwListBox.getChildren().addAll(pwList, newPwBtn);

		// add searchTf and button to searchBox and set padding
		searchBox.setSpacing(5);
		searchBox.getChildren().addAll(searchTf, searchBtn);
		searchBox.setPadding(new Insets(5, 5, 5, 5));

		// set up pwViewGridPane
		pwViewGridPane.setVgap(5);
		pwViewGridPane.setHgap(5);
		pwViewGridPane.add(titleLbl, 0, 0);
		pwViewGridPane.add(titleTf, 1, 0);
		pwViewGridPane.add(urlLbl, 0, 1);
		pwViewGridPane.add(urlTf, 1, 1);
		pwViewGridPane.add(userNameLbl, 0, 2);
		pwViewGridPane.add(userNameTf, 1, 2);
		pwViewGridPane.add(passwordLbl, 0, 3);
		pwViewGridPane.add(passwordTf, 1, 3);

		// set up pwEditButtonHBox
		pwEditButtonHBox.setSpacing(15);
		pwEditButtonHBox.getChildren().addAll(saveChangesBtn, deleteBtn);
		pwEditButtonHBox.setPadding(new Insets(5, 5, 5, 5));

		// set up pwViewAndEditVBox
		pwViewAndEditVBox.setSpacing(15);
		pwViewAndEditVBox.getChildren().addAll(pwViewGridPane, noteLbl, noteTa, pwEditButtonHBox);
		pwViewAndEditVBox.setPadding(new Insets(5, 5, 5, 5));

		// set up mainViewHBox
		mainViewHBox.getChildren().addAll(pwListBox, pwViewAndEditVBox);
		mainViewHBox.setPadding(new Insets(5, 5, 5, 5));

		// make responsive
		menuBar.prefWidthProperty().bind(widthProperty());
		pwList.prefWidthProperty().bind(widthProperty().divide(2));
		pwViewAndEditVBox.prefWidthProperty().bind(widthProperty().divide(2));
		noteTa.prefWidthProperty().bind(pwViewAndEditVBox.widthProperty().subtract(10));
		

		// put all of these things that were just set up onto this object
		this.getChildren().addAll(menuBar, searchBox, mainViewHBox);

		pwList.getSelectionModel().selectedItemProperty().addListener(ov -> {
			pwViewGridPane.getChildren();

			for (Integer i : pwList.getSelectionModel().getSelectedIndices())
			{
				titleTf.setText(obserableSiteList.get(i).title);
				urlTf.setText(obserableSiteList.get(i).websiteUrl);
				userNameTf.setText(obserableSiteList.get(i).userName);
				passwordTf.setText(obserableSiteList.get(i).password);
				noteTa.setText(obserableSiteList.get(i).note);
				selectedSite = i;
				saveChangesBtn.setDisable(true);
			};
		});

		saveChangesBtn.setDisable(true);
		newPwBtn.setOnAction(addSiteHandler);
		saveChangesBtn.setOnAction(saveHandler);
		searchBtn.setOnAction(searchHandler);
		deleteBtn.setOnAction(deleteHandler);

		if (obserableSiteList.size() > 0)
		{
			titleTf.setText(obserableSiteList.get(0).title);
			urlTf.setText(obserableSiteList.get(0).websiteUrl);
			userNameTf.setText(obserableSiteList.get(0).userName);
			passwordTf.setText(obserableSiteList.get(0).password);
			noteTa.setText(obserableSiteList.get(0).note);
		}

		titleTf.setOnMouseClicked(e -> saveChangesBtn.setDisable(false));
		urlTf.setOnMouseClicked(e -> saveChangesBtn.setDisable(false));
		userNameTf.setOnMouseClicked(e -> saveChangesBtn.setDisable(false));
		passwordTf.setOnMouseClicked(e -> saveChangesBtn.setDisable(false));
		noteTa.setOnMouseClicked(e -> saveChangesBtn.setDisable(false));
	}

	public int getSelectedSite()
	{
		return this.selectedSite;
	}
}
