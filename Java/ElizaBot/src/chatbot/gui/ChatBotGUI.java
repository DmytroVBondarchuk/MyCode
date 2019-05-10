package chatbot.gui;

        import codeanticode.eliza.Eliza;
        import javafx.application.Application;
        import javafx.application.Platform;
        import javafx.event.ActionEvent;
        import javafx.event.EventHandler;
        import javafx.scene.Scene;
        import javafx.scene.control.*;
        import javafx.scene.layout.GridPane;
        import javafx.scene.layout.HBox;
        import javafx.scene.paint.Paint;
        import javafx.stage.Stage;


public class ChatBotGUI extends Application implements EventHandler<ActionEvent> {

    // YOUR CODE GOES HERE
    public TextArea chatField = new TextArea();
    TextField answerField = new TextField();
    public Eliza eliza = new Eliza();

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Eliza Bot");

        //menu
        Menu fileMenu = new Menu("File");

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> closeApp());

        MenuItem reset = new MenuItem("Reset");
        reset.setOnAction(e -> resetChat());

        fileMenu.getItems().add(reset);
        fileMenu.getItems().add(new SeparatorMenuItem());
        fileMenu.getItems().add(exit);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(fileMenu);


        //chat field

        chatField.setMaxWidth(400);
        chatField.setMinWidth(400);
        chatField.setPrefWidth(400);
        chatField.setMinHeight(300);
        chatField.setMaxHeight(300);
        chatField.setEditable(false);

        //your input field

        answerField.setPromptText("Feel free to ask me something!");
        answerField.setOnAction(event -> startConversation(answerField.getText()));
        answerField.setMaxHeight(50);
        answerField.setPrefHeight(50);
        answerField.setPrefWidth(300);
        answerField.setMaxWidth(300);


        //send button
        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> startConversation(answerField.getText()));
        sendButton.setStyle("-fx-font: 23 arial; -fx-base: #b6e7c9;");


        // Layout
        GridPane layout = new GridPane();
        layout.add(menuBar, 1, 1);
        layout.add(chatField, 2, 2, 100, 100);
        layout.add(answerField, 2, 150);
        layout.add(sendButton, 5, 150);

        Scene scene = new Scene(layout, 500, 390);

        primaryStage.setScene(scene);
        primaryStage.show();

        //greetings from Eliza
        chatField.appendText("Eliza: " + eliza.getGreeting()+ "\n");


    }

    @Override
    public void handle(ActionEvent event) {
        System.out.println("Button was pressed!");
    }

    public static void main(String[] args) {
        ChatBotGUI.launch(args);
    }

    public static void closeApp() {
        Platform.exit();
    }

    public void startConversation(String userInput) {
        if (userInput.equalsIgnoreCase("goodbye")) {
            chatField.appendText("Eliza: " + eliza.isFinished()+ "\n");
        } else {
            chatField.appendText("You: " + userInput + "\n");
            chatField.appendText("Eliza: " + eliza.processInput(userInput) + "\n");

        }
        answerField.clear();
    }
    public void resetChat(){
        chatField.setText("Conversation has been reset. \n");
    }


}
