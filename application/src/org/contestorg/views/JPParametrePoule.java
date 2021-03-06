package org.contestorg.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JComboBox;

import org.contestorg.common.Pair;
import org.contestorg.controllers.ContestOrg;
import org.contestorg.infos.InfosModelCategorie;
import org.contestorg.infos.InfosModelParticipant;
import org.contestorg.infos.InfosModelPoule;
import org.contestorg.infos.InfosThemeParametre;
import org.contestorg.interfaces.IChangeable;
import org.contestorg.interfaces.IChangeableListener;

/**
 * Panel d'un paramètre de thème d'exportation/diffusion de type "Poule"
 */
@SuppressWarnings("serial")
public class JPParametrePoule extends JPParametreAbstract implements IChangeable<Pair<Integer,Integer>>, IChangeableListener<Integer>, ItemListener
{
	/** Poule */
	private JComboBox jcb_poule = new JComboBox();
	
	/** Couleur par défaut de la liste */
	private Color color;
	
	/** Panel dépendant d'un autre ? */
	private boolean dependant = false;

	/** Listeners */
	private ArrayList<IChangeableListener<Pair<Integer,Integer>>> listeners = new ArrayList<IChangeableListener<Pair<Integer,Integer>>>();
	
	/** Id de la catégorie */
	private Integer idCategorie = null;
	
	/** Ids des poules */
	private ArrayList<Integer> idsPoules = new  ArrayList<Integer>();

	/**
	 * Constructeur
	 * @param parametre paramètre
	 */
	public JPParametrePoule(InfosThemeParametre parametre) {
		// Appel du constructeur parent
		super(parametre);
		
		// Ajouter la liste dans le panel
		this.setLayout(new BorderLayout());
		this.add(this.jcb_poule);
		
		// Retenir la couleur par défaut de la liste
		this.color = this.jcb_poule.getBackground();
		
		// Remplir la liste
		if(ContestOrg.get().is(ContestOrg.STATE_OPEN)) {
			this.jcb_poule.addItem("Veuillez séléctionner une catégorie");
			this.jcb_poule.setBackground(new Color(250, 180, 76));
		} else {
			this.jcb_poule.addItem("Pas encore de poules");
			this.jcb_poule.setBackground(new Color(250, 90, 90));
		}
		
		// Ecouter la liste
		this.jcb_poule.addItemListener(this);
	}
	
	/**
	 * Récupérer l'id de la poule séléctionnée
	 * @return id de la poule séléctionnée
	 */
	private Integer getIdPoule() {
		return this.jcb_poule.getItemCount() == 0 || this.jcb_poule.getSelectedIndex() == 0 ? null : this.idsPoules.get(this.jcb_poule.getSelectedIndex()-1);
	}

	/**
	 * @see JPParametreAbstract#getValeur()
	 */
	@Override
	public String getValeur () {
		Integer idPoule = this.getIdPoule();
		return idPoule == null ? null : String.valueOf(idPoule);
	}
	
	/**
	 * @see JPParametreAbstract#setValeur(String)
	 */
	@Override
	public void setValeur (String valeur) {
		try {
			this.jcb_poule.setSelectedIndex(valeur == null ? 0 : this.idsPoules.indexOf(Integer.parseInt(valeur))+1);
		} catch(NumberFormatException e) { }
	}
	
	/**
	 * @see JPParametreAbstract#getError()
	 */
	@Override
	public String getError () {
		// Vérifier si le paramètre est optionnel
		if(!this.parametre.isOptionnel()) {
			// Vérifier si le concours est bien ouvert
			if(!ContestOrg.get().is(ContestOrg.STATE_OPEN)) {
				return "Le paramètre \""+this.parametre.getNom()+"\" ne peut pas être défini tant que le concours n'est pas créé.";
			}
			
			// Vérifier si le composant est dépendant d'un autre
			if(!this.dependant) {
				return "Le paramètre \""+this.parametre.getNom()+"\" doit être dépendant d'une catégorie.";
			}
			
			// Vérifier si la valeur n'est pas vide
			if(this.getValeur() == null || this.getValeur().isEmpty()) {
				return "Le paramètre \""+this.parametre.getNom()+"\" est obligatoire.";
			}
		}
		
		// Retourner null
		return null;
	}

	/**
	 * @see JPParametreAbstract#link(JPParametreAbstract[])
	 */
	@Override
	public void link (JPParametreAbstract[] panels) {
		if(ContestOrg.get().is(ContestOrg.STATE_OPEN)) {
			// Chercher le composant de la catégorie
			for(JPParametreAbstract composant : panels) {
				if(composant instanceof JPParametreCategorie && this.isDependant(composant)) {
					// Ecouter le composant
					((JPParametreCategorie)composant).addListener(this);
					
					// Retenir la dépendance
					this.dependant = true;
				}
			}
			
			// Erreur si le composant courant n'est pas dépendant d'un autre
			if(!this.dependant) {
				this.jcb_poule.removeAllItems();
				this.jcb_poule.addItem("Dépendance avec une catégorie manquante");
				this.jcb_poule.setBackground(new Color(250, 90, 90));
			}
		}
	}

	// Implémentation de IChangeable
	@Override
	public void addListener (IChangeableListener<Pair<Integer,Integer>> listener) {
		this.listeners.add(listener);
		listener.change(new Pair<Integer,Integer>(this.idCategorie,this.getIdPoule()));
	}
	@Override
	public void removeListener (IChangeableListener<Pair<Integer,Integer>> listener) {
		this.listeners.remove(listener);
	}

	/**
	 * @see IChangeableListener#change(Object)
	 */
	@Override
	public void change (Integer idCategorie) {
		// Vider la liste des poules
		this.jcb_poule.removeAllItems();
		this.idsPoules.clear();
		
		// Retenir l'id de la catégorie
		this.idCategorie = idCategorie;
		
		// Vérifier si une catégorie a été choisie
		if(this.idCategorie == null) {
			this.jcb_poule.addItem("Veuillez séléctionner une catégorie");
			this.jcb_poule.setBackground(new Color(250, 180, 76));
			return;
		}
		
		// Ajouter les poules de la catégorie
		boolean trouvee = false;
		ArrayList<Pair<InfosModelCategorie, ArrayList<Pair<InfosModelPoule, ArrayList<InfosModelParticipant>>>>> categories = ContestOrg.get().getCtrlParticipants().getListeCategoriesPoulesParticipants();
		for(Pair<InfosModelCategorie, ArrayList<Pair<InfosModelPoule, ArrayList<InfosModelParticipant>>>> categorie : categories) {
			if(this.idCategorie.equals(categorie.getFirst().getId())) {
				// Catégorie trouvée
				trouvee = true;
				
				// Remplir la liste
				if(categorie.getSecond().size() != 0) {
					this.jcb_poule.addItem("Veuillez séléctionner une poule");
					this.jcb_poule.setBackground(this.color);
					for(Pair<InfosModelPoule, ArrayList<InfosModelParticipant>> poule : categorie.getSecond()) {
						this.jcb_poule.addItem(poule.getFirst().getNom());
						this.idsPoules.add(poule.getFirst().getId());
					}
					if(this.jcb_poule.getItemCount() == 2) {
						this.jcb_poule.setSelectedIndex(1);
					}
				} else {
					this.jcb_poule.addItem("Pas encore de poules");
					this.jcb_poule.setBackground(new Color(250, 90, 90));
				}
			}
		}
		if(!trouvee) {
			this.jcb_poule.addItem("Catégorie correspondante non trouvée");
			this.jcb_poule.setBackground(new Color(250, 90, 90));
		}
	}

	/**
	 * @see ItemListener#itemStateChanged(ItemEvent)
	 */
	@Override
	public void itemStateChanged (ItemEvent event) {
		if(event.getStateChange() == ItemEvent.SELECTED) {
			// Fire des listeners
			for(IChangeableListener<Pair<Integer,Integer>> listener : this.listeners) {
				listener.change(new Pair<Integer,Integer>(this.idCategorie,this.getIdPoule()));
			}
		}
	}
	
}
