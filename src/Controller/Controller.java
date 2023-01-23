package Controller;

import java.io.IOException;
import java.util.List;

import Model.Read;
import View.GUI;

public class Controller {
    private GUI view;
    
    public Controller() {
        this.view = new GUI(this);
    }

    public void checkFile(List<String> files) throws IOException {
        Read test = new Read(this, files);
    }

    public void writeStatusInfo(String text) {
        view.setStatusInfo(text);
    }
}
