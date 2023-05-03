import org.engine.OneResult;

import javax.swing.*;

public class ResultLabel extends JLabel {
    OneResult result;
    public ResultLabel(OneResult res) {
        this.result = res;
        super.setText(this.result.toString());
    }
}
