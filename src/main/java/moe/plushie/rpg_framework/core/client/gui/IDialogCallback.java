package moe.plushie.rpg_framework.core.client.gui;

public interface IDialogCallback {
    
    public void dialogResult(AbstractGuiDialog dialog, DialogResult result);
    
    public static enum DialogResult {
        OK, CANCEL
    }
}
