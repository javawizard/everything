package tests;

import java.io.File;

import net.sf.convergia.client.Storage;
import net.sf.convergia.client.workspace.WorkspaceWrapper;


public class Test009
{
	public static void main(String[] args)
	{
		Storage.initStorage(new File("appdata"));
		Storage.setCurrentUser("test3");
		WorkspaceWrapper ww = new WorkspaceWrapper();
		ww.setId("test1-57483");
		ww.setName("Test Workspace");
		ww.setTypeId("i_w_ex_schat");
		ww.setDatastore(new File(Storage.getWorkspaceDataStore(), ww.getId()));
		Storage.addOrUpdateWorkspace(ww);
	}
}
