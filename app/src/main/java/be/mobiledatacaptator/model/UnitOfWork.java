package be.mobiledatacaptator.model;

import be.mobiledatacaptator.dao.DropBoxDao;
import be.mobiledatacaptator.dao.IMdcDao;

public class UnitOfWork {

	private static UnitOfWork instance;
	private static IMdcDao dao;

	private Project activeProject;
	private Fiche activeFiche;

	private UnitOfWork() {
	}

	public static UnitOfWork getInstance() {
		if (instance == null) {
			instance = new UnitOfWork();
			dao = new DropBoxDao();
		}
		return instance;
	}

	public IMdcDao getDao() {
		return dao;
	}

	public Project getActiveProject() {
		return activeProject;
	}

	public void setActiveProject(Project activeProject) {
		this.activeProject = activeProject;
	}

	public Fiche getActiveFiche() {
		return activeFiche;
	}

	public void setActiveFiche(Fiche activeFiche) {
		this.activeFiche = activeFiche;
	}

}
