﻿package org.contestorg.views;


import java.awt.Window;

import org.contestorg.common.Pair;
import org.contestorg.infos.InfosModelDiffusion;
import org.contestorg.infos.InfosModelTheme;
import org.contestorg.interfaces.ICollector;


@SuppressWarnings("serial")
public class JDDiffusionEditer extends JDDiffusionAbstract
{

	// Constructeur
	public JDDiffusionEditer(Window w_parent, ICollector<Pair<InfosModelDiffusion,InfosModelTheme>> collector, Pair<InfosModelDiffusion,InfosModelTheme> infos) {
		// Appeller le constructeur du parent
		super(w_parent, "Editer une diffusion", collector);

		// Remplir les champs avec les données de la diffusion
		this.jtf_nom.setText(infos.getFirst().getNom());
		this.jtf_port.setText(String.valueOf(infos.getFirst().getPort()));
		this.jp_theme.setTheme(infos.getSecond());
	}

}