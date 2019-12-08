package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import controller.LoginController;

/**
 * This class is the entry point of the Photo Library Application.
 * 
 * @author Umang Patel
 * @author Akashkumar Patel
 */
public class Photos extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * Initiates application by creating a stage of the login view.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/view/Loginfxml.fxml"));
			AnchorPane root = (AnchorPane)loader.load();
			LoginController con = (LoginController)(loader.getController());
			con.init();
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Photo Album Login Page");
			primaryStage.setResizable(false);  
			primaryStage.show();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}	
}