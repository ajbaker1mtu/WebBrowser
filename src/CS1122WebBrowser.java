// IMPORTS
// These are some classes that may be useful for completing the project.
// You may have to add others.
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebHistory.Entry;
import javafx.stage.Stage;
import javafx.concurrent.Worker.State;
import javafx.concurrent.Worker;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * The main class for CS1122WebBrowser. CS1122WebBrowser constructs the JavaFX window and
 * handles interactions with the dynamic components contained therein.
 *
 * Date Last Modified: 10/01/2023
 * @author Chris Sargent, Alex Baker
 *
 * CS1122, Fall 2023
 * Lab Section 2
 */
public class CS1122WebBrowser extends Application {
	// INSTANCE VARIABLES
	// These variables are included to get you started.
	private Stage primaryStage = null;
	private BorderPane borderPane = null;
	private WebView view = null;
	private WebEngine webEngine = null;
	private TextField statusbar = null;
	// HELPER METHODS
	/**
	 * Retrieves the value of a command line argument specified by the index.
	 *
	 * @param index - position of the argument in the args list.
	 * @return The value of the command line argument.
	 */
	private String getParameter( int index ) {
		Parameters params = getParameters();
		List<String> parameters = params.getRaw();
		return !parameters.isEmpty() ? parameters.get(index) : "";
	}

	/**
	 * Creates a WebView which handles mouse and some keyboard events, and
	 * manages scrolling automatically, so there's no need to put it into a ScrollPane.
	 * The associated WebEngine is created automatically at construction time.
	 *
	 * @return browser - a WebView container for the WebEngine.
	 */
	private WebView makeHtmlView( ) {
		view = new WebView();
		webEngine = view.getEngine();
		return view;
	}

	/**
	 * Generates the status bar layout and text field.
	 *
	 * @return statusbarPane - the HBox layout that contains the statusbar.
	 */
	private HBox makeStatusBar( ) {
		HBox statusbarPane = new HBox();
		statusbarPane.setPadding(new Insets(5, 4, 5, 4));
		statusbarPane.setSpacing(10);
		statusbarPane.setStyle("-fx-background-color: #336699;");
		statusbar = new TextField();
		HBox.setHgrow(statusbar, Priority.ALWAYS);
		statusbarPane.getChildren().addAll(statusbar);
		return statusbarPane;
	}

	// REQUIRED METHODS
	/**
	 * The main entry point for all JavaFX applications. The start method is
	 * called after the init method has returned, and after the system is ready
	 * for the application to begin running.
	 *
	 * NOTE: This method is called on the JavaFX Application Thread.
	 *
	 * @param stage - the primary stage for this application, onto which
	 * the application scene can be set.
	 */
	@Override
	public void start(Stage stage) {

		borderPane = new BorderPane();
		TextField search = new TextField();

		Insets inset = new Insets(0, 20, 0, 15);

		Button buttonBack = new Button("  Back  ");
		Button buttonForward = new Button("Forward");
		Button buttonHelp = new Button("Help");

		HBox tBox = new HBox(buttonBack, buttonForward, search, buttonHelp);

		tBox.setAlignment(Pos.CENTER);
		HBox.setHgrow(search, Priority.ALWAYS);
		search.setMaxWidth(Double.MAX_VALUE);

		HBox.setMargin(search, inset);
		HBox.setMargin(buttonBack, new Insets(0, 0, 0, 150));

		tBox.setMaxHeight(75);
		tBox.setPrefHeight(75);

		//makes the background of the search bar and buttons gray
		tBox.setBackground(
				new Background(
						new BackgroundFill(Color.GRAY,
								CornerRadii.EMPTY,
								Insets.EMPTY ) ) );

		Pane webPane = new Pane(makeHtmlView());

		borderPane.setTop(tBox);
		tBox.setSpacing(12);
		tBox.setFillHeight(false);
		HBox.setMargin(buttonBack, Insets.EMPTY);


		//makes the web page load when the enter key is pressed
		search.setOnKeyPressed(keyEvent -> {
			if (keyEvent.getCode() == KeyCode.ENTER) {
				String url = search.getText();
				if (!url.isEmpty()) {
					webEngine.load(url);
				}
			}
		});

		//button actions to ake them do what they are supposed to
		buttonBack.setOnAction(actionEvent -> goBack());
		buttonForward.setOnAction(actionEvent -> goForward());
		buttonHelp.setOnAction(actionEvent -> getHelp());


		//makes the status bar display the url being moused over
		webEngine.setOnStatusChanged(WebEvent -> {
			statusbar.setText(WebEvent.getData());
		});


		borderPane.setCenter(view);
		borderPane.setBottom(makeStatusBar());

		//code to set the tile of the application to the title of the webpage
		webEngine.getLoadWorker().stateProperty().addListener(
				new ChangeListener<State>() {
					public void changed(ObservableValue ov, State oldState, State newState) {
						if (newState == State.SUCCEEDED) {
							stage.setTitle(webEngine.getTitle());
						}
					}
				});

		Scene scene = new Scene(borderPane, 600, 450 );
		stage.setScene(scene);
		stage.show();


	}

	/**
	 * Returns the web viewer to the previous page.
	 */
	private void goBack() {
		WebHistory webHistory = webEngine.getHistory();
		if (webHistory.getCurrentIndex() > 0) {
			webHistory.go(-1);
		}
	}

	/**
	 * Returns the web viewer to the next page if available.
	 */
	private void goForward() {
		WebHistory webHistory = webEngine.getHistory();
		if (webHistory.getCurrentIndex() < webHistory.getEntries().size() - 1) {
			webHistory.go(+1);
		}
	}

	/**
	 * Displays the help page.
	 */
	private void getHelp() {
		String help = "<html>\n" +
				"<body>\n" +
				"<h1>Help Page</h1>\n" +
				"<p>Enter a complete and valid URL (i.e. http://www.mtu.edu) in the address bar above and hit the Enter key to navigate to a web page.</p>\n" +
				"<h2>Created By</h2>\n" +
				"<ul>\n" +
				"    <li>Alex Baker</li>\n" +
				"    <li>Chris Sargent</li>\n" +
				"</ul>\n" +
				"<h4>CS1122  Lab Section 2</h4>" +
				"</body>\n" +
				"</html>";
		view.getEngine().loadContent(help);
	}

	/**
	 * The main( ) method is ignored in JavaFX applications.
	 * main( ) serves only as fallback in case the application is launched
	 * as a regular Java application, e.g., in IDEs with limited FX
	 * support.
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
