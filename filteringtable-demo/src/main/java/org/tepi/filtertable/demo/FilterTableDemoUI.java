package org.tepi.filtertable.demo;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import javax.servlet.annotation.WebServlet;

import org.tepi.filtertable.FilterTable;
import org.tepi.filtertable.FilterTreeTable;
import org.tepi.filtertable.paged.PagedFilterControlConfig;
import org.tepi.filtertable.paged.PagedFilterTable;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Container.Filterable;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.HierarchicalContainer;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.RowHeaderMode;

@SuppressWarnings("serial")
@Title("FilterTable Demo Application")
@Theme("valo")
public class FilterTableDemoUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = FilterTableDemoUI.class, widgetset = "org.tepi.filtertable.demo.FilterTableDemoWidgetset")
	public static class Servlet extends VaadinServlet {
	}

	/**
	 * Example enum for enum filtering feature
	 */
	enum State {
		CREATED, PROCESSING, PROCESSED, FINISHED;
	}

	@Override
	protected void init(VaadinRequest request) {
		setLocale(new Locale("fi", "FI"));
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setMargin(new MarginInfo(true, false, false, false));
		mainLayout.setSizeFull();

		final TabSheet ts = new TabSheet();
		ts.setStyleName(ValoTheme.TABSHEET_COMPACT_TABBAR);
		ts.setSizeFull();
		mainLayout.addComponent(ts);
		mainLayout.setExpandRatio(ts, 1);

		ts.addTab(buildTableTab(), "Normal Table");
		ts.addTab(buildNormalTableTab(), "Normal FilterTable");
		ts.addTab(buildPagedTableTab(), "Paged FilterTable");
		ts.addTab(buildTreeTableTab(), "FilterTreeTable");

		setContent(mainLayout);
	}

	private Component buildTableTab() {
		/* Create FilterTable */
		final Table normalFilterTable = buildTable();
		final VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		mainLayout.addComponent(normalFilterTable);
		mainLayout.setExpandRatio(normalFilterTable, 1);

		Button addFilter = new Button("Add a filter");
		addFilter.addClickListener(event -> {
			Filterable f = ((Filterable) normalFilterTable.getContainerDataSource());
			f.addContainerFilter(new Filter() {

				@Override
				public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException {
					return item != null && ((String) item.getItemProperty("name").getValue()).contains("5");
				}

				@Override
				public boolean appliesToProperty(Object propertyId) {
					return "name".equals(propertyId);
				}
			});
		});
		mainLayout.addComponent(addFilter);

		Panel p = new Panel();
		p.setSizeFull();
		p.setContent(mainLayout);

		return p;
	}

	private Component buildNormalTableTab() {
		/* Create FilterTable */
		FilterTable normalFilterTable = buildFilterTable();
		final VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		mainLayout.addComponent(normalFilterTable);
		mainLayout.setExpandRatio(normalFilterTable, 1);
		mainLayout.addComponent(buildButtons(normalFilterTable));

		Panel p = new Panel();
		p.setSizeFull();
		p.setContent(mainLayout);

		return p;
	}

	private Table buildTable() {
		Table filterTable = new Table();
		filterTable.setSizeFull();

		filterTable.setSelectable(true);
		filterTable.setImmediate(true);
		filterTable.setMultiSelect(true);

		filterTable.setColumnCollapsingAllowed(true);

		filterTable.setColumnReorderingAllowed(true);

		filterTable.setContainerDataSource(buildContainer());

		// filterTable.setColumnCollapsed("state", true);

		filterTable
				.setVisibleColumns((Object[]) new String[] { "name", "id", "state", "date", "validated", "checked", "comp" });

		filterTable
				.setItemDescriptionGenerator((source, itemId, propertyId) -> "Just testing ItemDescriptionGenerator");

		return filterTable;
	}

	private FilterTable buildFilterTable() {
		FilterTable filterTable = new FilterTable();
		filterTable.setSizeFull();
		filterTable.setFilterDecorator(new DemoFilterDecorator());
		filterTable.setFilterGenerator(new DemoFilterGenerator());

		filterTable.setFilterBarVisible(true);

		filterTable.setSelectable(true);
		filterTable.setImmediate(true);
		filterTable.setMultiSelect(true);

		filterTable.setRowHeaderMode(RowHeaderMode.INDEX);

		filterTable.setColumnCollapsingAllowed(true);

		filterTable.setColumnReorderingAllowed(true);

		filterTable.setContainerDataSource(buildContainer());

		// filterTable.setColumnCollapsed("state", true);

		filterTable
				.setVisibleColumns((Object[]) new String[] { "name", "id", "state", "date", "validated", "checked", "comp" });

		filterTable
				.setItemDescriptionGenerator((source, itemId, propertyId) -> "Just testing ItemDescriptionGenerator");
		filterTable.setColumnHeaderStylename("id", "headerStylename-testing");
		return filterTable;
	}

	private Component buildPagedTableTab() {
		/* Create FilterTable */
		PagedFilterTable<IndexedContainer> pagedFilterTable = buildPagedFilterTable();

		final VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		mainLayout.addComponent(pagedFilterTable);
		mainLayout.addComponent(pagedFilterTable.createControls(new PagedFilterControlConfig()));
		mainLayout.addComponent(buildButtons(pagedFilterTable));
		return mainLayout;
	}

	private PagedFilterTable<IndexedContainer> buildPagedFilterTable() {
		PagedFilterTable<IndexedContainer> filterTable = new PagedFilterTable<IndexedContainer>();
		filterTable.setWidth("100%");

		filterTable.setFilterDecorator(new DemoFilterDecorator());
		filterTable.setFilterGenerator(new DemoFilterGenerator());

		filterTable.setFilterBarVisible(true);

		filterTable.setSelectable(true);
		filterTable.setImmediate(true);
		filterTable.setMultiSelect(true);

		filterTable.setRowHeaderMode(RowHeaderMode.INDEX);

		filterTable.setColumnCollapsingAllowed(true);

		filterTable.setColumnReorderingAllowed(true);

		filterTable.setContainerDataSource(buildContainer());

		filterTable.setPageLength(10);
		
		// filterTable.setColumnCollapsed("state", true);

		filterTable
				.setVisibleColumns((Object[]) new String[] { "name", "id", "state", "date", "validated", "checked" });

		return filterTable;
	}

	private Component buildTreeTableTab() {
		/* Create FilterTable */
		FilterTreeTable filterTreeTable = buildFilterTreeTable();

		final VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(true);
		mainLayout.addComponent(filterTreeTable);
		mainLayout.setExpandRatio(filterTreeTable, 1);
		mainLayout.addComponent(buildButtons(filterTreeTable));

		Panel p = new Panel();
		p.setSizeFull();
		p.setContent(mainLayout);

		return p;
	}

	private FilterTreeTable buildFilterTreeTable() {
		FilterTreeTable filterTable = new FilterTreeTable();
		filterTable.setSizeFull();

		filterTable.setFilterDecorator(new DemoFilterDecorator());
		filterTable.setFilterGenerator(new DemoFilterGenerator());

		filterTable.setFilterBarVisible(true);

		filterTable.setSelectable(true);
		filterTable.setImmediate(true);
		filterTable.setMultiSelect(true);

		filterTable.setRowHeaderMode(RowHeaderMode.INDEX);

		filterTable.setColumnCollapsingAllowed(true);

		filterTable.setColumnReorderingAllowed(true);

		filterTable.setContainerDataSource(buildHierarchicalContainer());
		// filterTable.setColumnCollapsed("state", true);

		filterTable
				.setVisibleColumns((Object[]) new String[] { "name", "id", "state", "date", "validated", "checked" });

		return filterTable;
	}

	private Component buildButtons(final FilterTable relatedFilterTable) {
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setHeight(null);
		buttonLayout.setWidth("100%");
		buttonLayout.setSpacing(true);

		Label hideFilters = new Label("Show Filters:");
		hideFilters.setSizeUndefined();
		buttonLayout.addComponent(hideFilters);
		buttonLayout.setComponentAlignment(hideFilters, Alignment.MIDDLE_LEFT);

		for (Object propId : relatedFilterTable.getContainerPropertyIds()) {
			Component t = createToggle(relatedFilterTable, propId);
			buttonLayout.addComponent(t);
			buttonLayout.setComponentAlignment(t, Alignment.MIDDLE_LEFT);
		}

		CheckBox showFilters = new CheckBox("Toggle Filter Bar visibility");
		showFilters.setValue(relatedFilterTable.isFilterBarVisible());
		showFilters.addValueChangeListener(event -> relatedFilterTable.setFilterBarVisible((Boolean) event.getValue()));
		buttonLayout.addComponent(showFilters);
		buttonLayout.setComponentAlignment(showFilters, Alignment.MIDDLE_RIGHT);
		buttonLayout.setExpandRatio(showFilters, 1);

		final Button runNow = new Button("Filter now");
		runNow.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				relatedFilterTable.runFilters();
			}
		});

		CheckBox runOnDemand = new CheckBox("Filter lazily");
		runOnDemand.setValue(relatedFilterTable.isFilterOnDemand());
		runNow.setEnabled(relatedFilterTable.isFilterOnDemand());
		runOnDemand.addValueChangeListener(event -> {
			boolean value = event.getValue();
			relatedFilterTable.setFilterOnDemand(value);
			runNow.setEnabled(value);
		});
		buttonLayout.addComponent(runOnDemand);
		buttonLayout.setComponentAlignment(runOnDemand, Alignment.MIDDLE_RIGHT);
		buttonLayout.addComponent(runNow);

		Button setVal = new Button("Set the State filter to 'Processed'");
		setVal.addClickListener(event -> relatedFilterTable.setFilterFieldValue("state", State.PROCESSED));
		buttonLayout.addComponent(setVal);

		Button reset = new Button("Reset");
		reset.addClickListener(event -> relatedFilterTable.resetFilters());
		buttonLayout.addComponent(reset);

		Button clear = new Button("Clear");
		clear.addClickListener(event -> relatedFilterTable.clearFilters());
		buttonLayout.addComponent(clear);

		return buttonLayout;
	}

	private Component buildButtons(final FilterTreeTable relatedFilterTable) {
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setHeight(null);
		buttonLayout.setWidth("100%");
		buttonLayout.setSpacing(true);

		Label hideFilters = new Label("Show Filters:");
		hideFilters.setSizeUndefined();
		buttonLayout.addComponent(hideFilters);
		buttonLayout.setComponentAlignment(hideFilters, Alignment.MIDDLE_LEFT);

		for (Object propId : relatedFilterTable.getContainerPropertyIds()) {
			Component t = createToggle(relatedFilterTable, propId);
			buttonLayout.addComponent(t);
			buttonLayout.setComponentAlignment(t, Alignment.MIDDLE_LEFT);
		}

		CheckBox showFilters = new CheckBox("Toggle Filter Bar visibility");
		showFilters.setValue(relatedFilterTable.isFilterBarVisible());
		showFilters.addValueChangeListener(event -> relatedFilterTable.setFilterBarVisible(event.getValue()));
		buttonLayout.addComponent(showFilters);
		buttonLayout.setComponentAlignment(showFilters, Alignment.MIDDLE_RIGHT);
		buttonLayout.setExpandRatio(showFilters, 1);

		CheckBox wrapFilters = new CheckBox("Wrap Filter Fields");
		wrapFilters.setValue(relatedFilterTable.isWrapFilters());
		wrapFilters.addValueChangeListener(event -> relatedFilterTable.setWrapFilters(event.getValue()));
		buttonLayout.addComponent(wrapFilters);
		buttonLayout.setComponentAlignment(wrapFilters, Alignment.MIDDLE_RIGHT);

		final Button runNow = new Button("Filter now");
		runNow.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				relatedFilterTable.runFilters();
			}
		});

		CheckBox runOnDemand = new CheckBox("Filter lazily");
		runOnDemand.setValue(relatedFilterTable.isFilterOnDemand());
		runNow.setEnabled(relatedFilterTable.isFilterOnDemand());
		runOnDemand.addValueChangeListener(event -> {
			boolean value = event.getValue();
			relatedFilterTable.setFilterOnDemand(value);
			runNow.setEnabled(value);
		});
		buttonLayout.addComponent(runOnDemand);
		buttonLayout.setComponentAlignment(runOnDemand, Alignment.MIDDLE_RIGHT);
		buttonLayout.addComponent(runNow);

		Button setVal = new Button("Set the State filter to 'Processed'");
		setVal.addClickListener(event -> relatedFilterTable.setFilterFieldValue("state", State.PROCESSED));
		buttonLayout.addComponent(setVal);

		Button reset = new Button("Reset");
		reset.addClickListener(event -> relatedFilterTable.resetFilters());
		buttonLayout.addComponent(reset);

		Button clear = new Button("Clear");
		clear.addClickListener(event -> relatedFilterTable.clearFilters());
		buttonLayout.addComponent(clear);

		return buttonLayout;
	}

	@SuppressWarnings("unchecked")
	private Container buildContainer() {
		IndexedContainer cont = new IndexedContainer();
		Calendar c = Calendar.getInstance();

		cont.addContainerProperty("name", String.class, null);
		cont.addContainerProperty("id", Integer.class, null);
		cont.addContainerProperty("state", State.class, null);
		cont.addContainerProperty("date", Timestamp.class, null);
		cont.addContainerProperty("validated", Boolean.class, null);
		cont.addContainerProperty("checked", Boolean.class, null);
		cont.addContainerProperty("comp", Component.class, null);

		Random random = new Random();
		for (int i = 0; i < 10000; i++) {
			cont.addItem(i);
			/* Set name and id properties */
			cont.getContainerProperty(i, "name").setValue("Order " + i);
			cont.getContainerProperty(i, "id").setValue(i);
			/* Set state property */
			int rndInt = random.nextInt(4);
			State stateToSet = State.CREATED;
			if (rndInt == 0) {
				stateToSet = State.PROCESSING;
			} else if (rndInt == 1) {
				stateToSet = State.PROCESSED;
			} else if (rndInt == 2) {
				stateToSet = State.FINISHED;
			}
			cont.getContainerProperty(i, "state").setValue(stateToSet);
			/* Set date property */
			cont.getContainerProperty(i, "date").setValue(new Timestamp(c.getTimeInMillis()));
			c.add(Calendar.DAY_OF_MONTH, 1);
			/* Set validated property */
			cont.getContainerProperty(i, "validated").setValue(random.nextBoolean());
			/* Set checked property */
			cont.getContainerProperty(i, "checked").setValue(random.nextBoolean());
			cont.getContainerProperty(i, "comp").setValue(new CheckBox());
		}
		return cont;
	}

	@SuppressWarnings("unchecked")
	private Container buildHierarchicalContainer() {
		HierarchicalContainer cont = new HierarchicalContainer();
		Calendar c = Calendar.getInstance();

		cont.addContainerProperty("name", String.class, null);
		cont.addContainerProperty("id", Integer.class, null);
		cont.addContainerProperty("state", State.class, null);
		cont.addContainerProperty("date", Date.class, null);
		cont.addContainerProperty("validated", Boolean.class, null);
		cont.addContainerProperty("checked", Boolean.class, null);

		Random random = new Random();
		int previousItemId = 0;
		for (int i = 0; i < 10000; i++) {
			cont.addItem(i);
			/* Setup parent/child relations */
			if (i % 5 == 0) {
				previousItemId = i;
			}
			cont.setChildrenAllowed(i, i == 0 || i % 5 == 0);
			if (previousItemId != i) {
				cont.setParent(i, previousItemId);
			}
			/* Set name and id properties */
			cont.getContainerProperty(i, "name").setValue("Order " + i);
			cont.getContainerProperty(i, "id").setValue(i);
			/* Set state property */
			int rndInt = random.nextInt(4);
			State stateToSet = State.CREATED;
			if (rndInt == 0) {
				stateToSet = State.PROCESSING;
			} else if (rndInt == 1) {
				stateToSet = State.PROCESSED;
			} else if (rndInt == 2) {
				stateToSet = State.FINISHED;
			}
			cont.getContainerProperty(i, "state").setValue(stateToSet);
			/* Set date property */
			cont.getContainerProperty(i, "date").setValue(c.getTime());
			c.add(Calendar.DAY_OF_MONTH, 1);
			/* Set validated property */
			cont.getContainerProperty(i, "validated").setValue(random.nextBoolean());
			/* Set checked property */
			cont.getContainerProperty(i, "checked").setValue(random.nextBoolean());
		}
		return cont;
	}

	private Component createToggle(final FilterTable relatedFilterTable, final Object propId) {
		CheckBox toggle = new CheckBox(propId.toString());
		toggle.setValue(relatedFilterTable.isFilterFieldVisible(propId));
		toggle.addValueChangeListener(event -> relatedFilterTable.setFilterFieldVisible(propId,
				!relatedFilterTable.isFilterFieldVisible(propId)));
		return toggle;
	}

	private Component createToggle(final FilterTreeTable relatedFilterTable, final Object propId) {
		CheckBox toggle = new CheckBox(propId.toString());
		toggle.setValue(relatedFilterTable.isFilterFieldVisible(propId));
		toggle.addValueChangeListener(event -> relatedFilterTable.setFilterFieldVisible(propId,
				!relatedFilterTable.isFilterFieldVisible(propId)));
		return toggle;
	}
}