package com.example.view;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainScreen extends VBox {
    private void onchangeInitialize(TextField emailField, PasswordField passwordField, Label emailErrorLabel,
            Label passwordErrorLabel, Label labelError) {
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isBlank()) {
                emailErrorLabel.setText("");
                labelError.setText("");
            }
        });

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isBlank()) {
                passwordErrorLabel.setText("");
                labelError.setText("");
            }
        });
    }

    public MainScreen(Stage stage) {
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        // onchangeInitialize(emailField, passwordField, emailErrorLabel,
        // passwordErrorLabel, labelError);

        VBox form = new VBox(5, emailLabel, emailField);
        form.setAlignment(Pos.CENTER);

        this.getChildren().add(form);
        this.setAlignment(Pos.CENTER);
    }

    public static void show(Stage stage) {
        Scene scene = new Scene(new MainScreen(stage));
        stage.setScene(scene);
        stage.show();
    }
}
