package moe.plushie.rpg_framework.core.client.gui;

public interface IDialogParent {
    
    public void openDialog(AbstractGuiDialog dialog);

    public boolean isDialogOpen();
    
    public void closeDialog();
}
