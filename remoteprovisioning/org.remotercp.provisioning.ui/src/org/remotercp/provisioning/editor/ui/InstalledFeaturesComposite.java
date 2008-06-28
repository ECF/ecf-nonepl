package org.remotercp.provisioning.editor.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.remotercp.common.provisioning.SerializedFeatureWrapper;
import org.remotercp.provisioning.editor.FeaturesLabelProvider;
import org.remotercp.provisioning.editor.FeaturesTreeContentProvider;
import org.remotercp.provisioning.editor.FeaturesTreeLabelProvider;

public class InstalledFeaturesComposite {

	private TableViewer featuresViewer;

	private TreeViewer differentFeaturesViewer;

	private Group commonFeaturesGroup;

	private Group differentFeaturesGroup;

	public InstalledFeaturesComposite(SashForm parent, int style) {

		this.createPartControl(parent, style);
	}

	private void createPartControl(SashForm parent, int style) {
		Composite main = new Composite(parent, SWT.None);
		main.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(main);

		{
			/*
			 * Sash for installed features, different features
			 */
			SashForm installedFeaturesSash = new SashForm(main, SWT.VERTICAL);
			installedFeaturesSash.setLayout(new GridLayout(1, false));
			GridDataFactory.fillDefaults().grab(true, true).applyTo(
					installedFeaturesSash);

			{
				commonFeaturesGroup = new Group(installedFeaturesSash, SWT.None);
				commonFeaturesGroup.setText("Common features");
				commonFeaturesGroup.setLayout(new GridLayout(1, false));
				GridDataFactory.fillDefaults().grab(true, true).applyTo(
						commonFeaturesGroup);
				{
					/*
					 * installed features tree viewer
					 */
					this.featuresViewer = new TableViewer(commonFeaturesGroup,
							SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
					GridDataFactory.fillDefaults().grab(true, true).applyTo(
							this.featuresViewer.getControl());
					this.featuresViewer
							.setContentProvider(new ArrayContentProvider());
					this.featuresViewer
							.setLabelProvider(new FeaturesLabelProvider());

					Table table = this.featuresViewer.getTable();

					TableColumn artifactName = new TableColumn(table, SWT.LEFT);
					artifactName.setText("Name");
					artifactName.setWidth(400);

					table.setLinesVisible(true);
					table.setHeaderVisible(true);

				}
			}
			{

				differentFeaturesGroup = new Group(installedFeaturesSash,
						SWT.None);
				differentFeaturesGroup.setText("Different features");
				differentFeaturesGroup.setLayout(new GridLayout(1, false));
				GridDataFactory.fillDefaults().grab(true, true).applyTo(
						differentFeaturesGroup);
				{

					/*
					 * different features of selected features will be shown
					 * here
					 */
					this.differentFeaturesViewer = new TreeViewer(
							differentFeaturesGroup, SWT.H_SCROLL | SWT.V_SCROLL);
					GridDataFactory.fillDefaults().grab(true, false).applyTo(
							this.differentFeaturesViewer.getControl());

					this.differentFeaturesViewer
							.setContentProvider(new FeaturesTreeContentProvider());
					this.differentFeaturesViewer
							.setLabelProvider(new FeaturesTreeLabelProvider());
				}
			}
			installedFeaturesSash.setWeights(new int[] { 2, 1 });
		}

		{
			Composite installedFeaturesButtonsComposite = new Composite(parent,
					SWT.None);
			installedFeaturesButtonsComposite
					.setLayout(new GridLayout(1, false));

			Button checkForUpdates = new Button(
					installedFeaturesButtonsComposite, SWT.PUSH);
			checkForUpdates.setText("Check for updates...");

			Button uninstall = new Button(installedFeaturesButtonsComposite,
					SWT.PUSH);
			uninstall.setText("Update");

			// hier k�nnen die URLs der Update site und �. eingesehen und
			// ge�ndert werden
			Button options = new Button(installedFeaturesButtonsComposite,
					SWT.PUSH);
			options.setText("Options");
		}
	}

	/**
	 * Sets the table viewer input
	 * 
	 * @param input
	 */
	public void setCommonFeaturesInput(Set<SerializedFeatureWrapper> input) {
		this.commonFeaturesGroup.setText("Common features: " + input.size());
		this.featuresViewer.setInput(input);
	}

	public void setDifferentFeaturesInput(
			Set<SerializedFeatureWrapper> input,
			Map<SerializedFeatureWrapper, Collection<ID>> differentFeaturesToUser) {

		Collection<TreeNode> featureNodes = new ArrayList<TreeNode>();

		// create tree nodes
		for (SerializedFeatureWrapper feature : input) {
			// feature node
			TreeNode featureNode = new TreeNode(feature);

			Collection<TreeNode> userNodes = new ArrayList<TreeNode>();
			Collection<ID> userIDs = differentFeaturesToUser.get(feature);

			// feature node children
			for (ID user : userIDs) {
				TreeNode userNode = new TreeNode(user);
				userNodes.add(userNode);
				userNode.setParent(featureNode);
			}

			featureNode.setChildren(userNodes.toArray(new TreeNode[userNodes
					.size()]));

			featureNodes.add(featureNode);

		}

		this.differentFeaturesGroup.setText("Different features:"
				+ featureNodes.size());
		this.differentFeaturesViewer.setInput(featureNodes);
	}
}
