package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Pair;
public class Controller implements Initializable{
	
	@FXML private ChoiceBox<String> ChoixConvexe;
	@FXML private ChoiceBox<String> ChoixTri;
	@FXML private Label choix;
	@FXML private Pane Dessin;
	
	Point2D P1,P2;
	private Circle point;
	private Line Jointure;
	ObservableList<String> ListConv = FXCollections.observableArrayList();
	ObservableList<String> ListTri = FXCollections.observableArrayList();

	private String path;
	private String pathOut;
	private int nb;
	private static ArrayList<String> list=new ArrayList<String>();	
	
	public void loadConvexe() {
		String SCH="SlowConvexHull";
		String CH="ConvexHull";
		String RCH="RapidConvexHull";
		ListConv.addAll(SCH,CH,RCH);
		ChoixConvexe.getItems().addAll(ListConv);
		ChoixConvexe.setValue(SCH);
	}
	
	public void loadTri() {
		String TpT="Tri par Tas";
		String TpA="Tri par Arbre";
		String TpE="Tri par Echange";
		ListTri.addAll(TpT,TpA,TpE);
		ChoixTri.getItems().addAll(ListTri);
		ChoixTri.setValue(TpT);
	}
	
	public void AffichageChoixTri() {
		if(ChoixConvexe.getValue().equals("SlowConvexHull")||
				ChoixConvexe.getValue().equals("RapidConvexHull")||
				ChoixConvexe.getValue().equals(""))
			ChoixTri.setDisable(true);
		else ChoixTri.setDisable(false);
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		loadConvexe();
		loadTri();
	}
	
	public void ChoixFichier(ActionEvent event) {
	//Creation de la boite de dialogue
		FileChooser fileChooser = new FileChooser();	
	//Parametrage...
		fileChooser.setTitle("Veuillez choisir un fichier de point");
		File homeDir = new File("data\\"/*System.getProperty("user.home")*/);
		fileChooser.setInitialDirectory(homeDir);
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("All Files" , "*.*"));
	
	//Affichage et gestion du resultat...
		File selectedFile = fileChooser.showOpenDialog(new Stage());
		if (selectedFile != null) {
			try {
				path = selectedFile.getPath();
				pathOut = selectedFile.getParent()+"\\out"+selectedFile.getName();
			}
			catch (Exception e) {
				System.err.println("Erreur: impossible d'utiliser le fichier");
			}
		}
	}
	public void CreerNbPoint(ActionEvent event) throws Exception {
		TextInputDialog InDialog=new TextInputDialog("");
		InDialog.setTitle("Saisissez le nombre de point voulu");	
		InDialog.setHeaderText(null);
		InDialog.setContentText("Votre nombre de points :");
		Optional<String> textIn = InDialog.showAndWait();
		if (textIn.isPresent()) {
			nb=Integer.parseInt(textIn.get());
			CreerPoint();
			CreerFichier("data\\");
		}
		else nb=0;
		
	}
	
	public void CreerPoint()throws Exception {
		for (int i =0;i<nb;i++) {
			Dialog<Pair<String,String>> dialog = new Dialog<>();
			dialog.setTitle("Creations des coordonnees du point "+(i+1));
			dialog.setHeaderText("Saisissez les coordonnees du point");
			
			ButtonType buttonTypeOk = new ButtonType("Ok",ButtonData.OK_DONE);
			ButtonType buttonTypeAnnuler = new ButtonType("Annuler",ButtonData.CANCEL_CLOSE);
			
			dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeAnnuler);
			
			GridPane grille = new GridPane();
			grille.setHgap(10);
			grille.setVgap(10);
			grille.setPadding(new Insets(20, 150, 10, 10));

			TextField CoordX = new TextField();
			TextField CoordY = new TextField();

			grille.add(new Label("Coordonnées X :"), 0, 0);
			grille.add(CoordX, 1, 0);
			grille.add(new Label("Coordonnées Y :"), 0, 1);
			grille.add(CoordY, 1, 1);
			
			dialog.getDialogPane().setContent(grille);
			
			dialog.setResultConverter(dialogButton-> {
				if (dialogButton == buttonTypeOk)
						return new Pair <>(CoordX.getText(), CoordY.getText());
				return null;
			});
			
			Optional<Pair<String, String>> result = dialog.showAndWait();
			result.ifPresent(pair -> {
				list.add(pair.getKey()+" "+pair.getValue());
			});
		}
	}
	
	public void CreerFichier(String dos) {
		TextInputDialog InDialog=new TextInputDialog("");
		InDialog.setTitle("Saisissez le nom du fichier");	
		InDialog.setHeaderText(null);
		InDialog.setContentText("Le nom du fichier avec son extension :");
		Optional<String> textIn = InDialog.showAndWait();
		if (textIn.isPresent()) {
			try {
				String s=textIn.get();
				PrintWriter writer = new PrintWriter(dos+s, "UTF-8");
				path = dos+s;
				writer.println(nb);
				for(String se : list)
						writer.println(se);
				writer.close();
				list.clear();nb=0;
				pathOut=dos+"out"+s;
			}
			catch (IOException e){
				e.printStackTrace();
			}
		}
	}
	
	public void OuvrirFichier(String s) throws FileNotFoundException {
			File file = new File(s);
			Scanner obj = new Scanner(file);
			nb=Integer.parseInt(obj.nextLine());
			while(obj.hasNextLine()) {
				list.add(obj.nextLine());
			}
			obj.close();
		}
	
	public void Run() throws FileNotFoundException {
		if(ChoixConvexe.getValue().equals("SlowConvexHull")||
				ChoixConvexe.getValue().equals("RapidConvexHull")) {
			choix.setText("Convexe : "+ChoixConvexe.getValue());
			/*if(ChoixConvexe.getValue().equals("SlowConvexHull"))
				new findConvexHull(path,pathOut,0,0);
			else new findConvexHull(path,pathOut,2,0);*/
		}
		else {			
			choix.setText("Convexe : "+ChoixConvexe.getValue()+" avec "
		+ChoixTri.getValue());
			/*if(ChoixTri.getValue().equals("Tri par Arbre"))
				findConvexHull(path,pathOut,1,1);
			
			else if (ChoixTri.getValue().equals("Tri par Tas"))
				findConvexHull(path,pathOut,1,2);
			else 
				findConvexHull(path,pathOut,1,3);*/
		}
		Dessiner(path, pathOut);
	}
	
	public void Dessiner(String infilename, String outfilename) throws FileNotFoundException {
		Dessin.getChildren().clear();
		OuvrirFichier(infilename);
		double maxH=Dessin.getHeight(); 
		for(String s : list) {
			String[] aux = s.split(" ");
			P1 = new Point2D(Integer.parseInt(aux[0]),Integer.parseInt(aux[1]));


			point=new Circle(P1.getX()*5,maxH-P1.getY()*5,2);
			point.setFill(Color.BLUE);
			
			Dessin.getChildren().add(point);
		}
		list.clear();
		OuvrirFichier(outfilename);
		Point2D PdebFichier = null;
		int i = 0;
		for(String s : list) {
			String[] aux = s.split(" ");
			
			if(i==0) {
				P2=new Point2D(Integer.parseInt(aux[0]),Integer.parseInt(aux[1]));
				PdebFichier= new Point2D(P2.getX(),P2.getY());
				i++;
			}
			
			else {
				P1=new Point2D(P2.getX(),P2.getY());
				P2=new Point2D(Integer.parseInt(aux[0]),Integer.parseInt(aux[1]));

				Jointure = new Line(P1.getX()*5,maxH-P1.getY()*5,P2
						.getX()*5,maxH-P2.getY()*5);
				Jointure.setFill(Color.RED);
				
				Dessin.getChildren().add(Jointure);
				i++;

				if(i==nb) {
					Jointure = new Line(PdebFichier.getX()*5,maxH-PdebFichier
							.getY()*5,P2.getX()*5,maxH-P2.getY()*5);
					Jointure.setFill(Color.RED);
					
					Dessin.getChildren().add(Jointure);
				}
			}	
		}
		list.clear();
	}
	
	
	public void arret(ActionEvent e) {
		Platform.exit();
	}
}
