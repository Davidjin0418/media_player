package model;

import javax.swing.text.StyledEditorKit;
import javax.swing.text.ViewFactory;

//taken from http://stackoverflow.com/questions/8666727/wrap-long-words-in-jtextpane-java-7 written by Stanislav Lapitsky
//to wrap long word within text pane to the next line
//---------------------------------------------------------------------------
public class WrapEditorKit extends StyledEditorKit {
  ViewFactory defaultFactory=new WrapColumnFactory();
  public ViewFactory getViewFactory() {
      return defaultFactory;
  }

}
