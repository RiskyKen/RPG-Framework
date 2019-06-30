package moe.plushie.rpgeconomy.core.client.gui;

public interface IDialogCallback {
    
    public void dialogResult(AbstractGuiDialog dialog, DialogResult result);
    
    public static enum DialogResult {
        OK, CANCEL
    }
}
