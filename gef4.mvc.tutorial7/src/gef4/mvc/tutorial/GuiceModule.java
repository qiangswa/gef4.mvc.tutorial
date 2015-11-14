package gef4.mvc.tutorial;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.inject.AdapterMaps;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.fx.MvcFxModule;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultFeedbackPartFactory;
import org.eclipse.gef4.mvc.fx.parts.VisualBoundsGeometryProvider;
import org.eclipse.gef4.mvc.fx.policies.FXDeleteSelectedOnTypePolicy;
import org.eclipse.gef4.mvc.fx.policies.FXFocusAndSelectOnClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXHoverOnHoverPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTranslateSelectedOnDragPolicy;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.fx.tools.FXHoverTool;
import org.eclipse.gef4.mvc.fx.tools.FXTypeTool;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;

import gef4.mvc.tutorial.parts.ContentPartFactory;
import gef4.mvc.tutorial.parts.FXDeleteHoverHandlePart;
import gef4.mvc.tutorial.parts.HandlePartFactory;
import gef4.mvc.tutorial.parts.TextNodePart;
import gef4.mvc.tutorial.policies.FXCreationMenuOnClickPolicy;
import gef4.mvc.tutorial.policies.FXDeleteFirstAnchorageOnClickPolicy;
import gef4.mvc.tutorial.policies.GlobalOnTypePolicy;
import gef4.mvc.tutorial.policies.TextNodeOnDoubleClickPolicy;
import gef4.mvc.tutorial.policies.TextNodeOnTypePolicy;
import gef4.mvc.tutorial.policies.TextNodeTransformPolicy;
import javafx.scene.Node;

@SuppressWarnings("serial")
public final class GuiceModule extends MvcFxModule {

	@Override
	protected void bindAbstractContentPartAdapters( MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractContentPartAdapters(adapterMapBinder);
		
		adapterMapBinder
			.addBinding(AdapterKey.get(FXClickDragTool.CLICK_TOOL_POLICY_KEY))
			.to(FXFocusAndSelectOnClickPolicy.class);
		
		adapterMapBinder
			.addBinding(AdapterKey.get(FXHoverTool.TOOL_POLICY_KEY))
			.to(FXHoverOnHoverPolicy.class);
		
		// geometry provider for selection feedback
		adapterMapBinder
			.addBinding(AdapterKey.get(
				new TypeToken<Provider<IGeometry>>(){}, 
				FXDefaultFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER))
			.to(VisualBoundsGeometryProvider.class);
		
		// geometry provider for hover feedback
		adapterMapBinder
			.addBinding(AdapterKey.get(
				new TypeToken<Provider<IGeometry>>(){}, 
				FXDefaultFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER))
			.to(VisualBoundsGeometryProvider.class);
	}

	protected void bindTextNodePartAdapters( MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		// register resize/transform policies (writing changes also to model)
		adapterMapBinder
			.addBinding(AdapterKey.get(FXTransformPolicy.class))
			.to(TextNodeTransformPolicy.class);
		
		// interaction policies to relocate on drag (including anchored elements, which are linked)
		adapterMapBinder
			.addBinding( AdapterKey.get(FXClickDragTool.DRAG_TOOL_POLICY_KEY))
			.to(FXTranslateSelectedOnDragPolicy.class);
		
		
		// edit node label policies
		adapterMapBinder
			.addBinding( AdapterKey.get( FXClickDragTool.CLICK_TOOL_POLICY_KEY, "TextNodeOnDoubleClickPolicy"))
			.to( TextNodeOnDoubleClickPolicy.class);
		
		adapterMapBinder
			.addBinding( AdapterKey.get( FXTypeTool.TOOL_POLICY_KEY, "TextNodeOnTypePolicy"))
			.to( TextNodeOnTypePolicy.class);

		adapterMapBinder
			.addBinding( AdapterKey.get( FXTypeTool.TOOL_POLICY_KEY, "GlobalOnTypePolicy"))
			.to( GlobalOnTypePolicy.class);

	}

	@Override
	protected void bindAbstractRootPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		super.bindAbstractRootPartAdapters(adapterMapBinder);
		
		adapterMapBinder
			.addBinding( AdapterKey.get( FXTypeTool.TOOL_POLICY_KEY, "GlobalOnTypePolicy"))
			.to( GlobalOnTypePolicy.class);

		adapterMapBinder
			.addBinding(AdapterKey.get(FXClickDragTool.CLICK_TOOL_POLICY_KEY, "FXCreationMenuOnClick"))
			.to(FXCreationMenuOnClickPolicy.class);
		
		// interaction policy to delete on key type
		adapterMapBinder.addBinding(AdapterKey.get(FXTypeTool.TOOL_POLICY_KEY))
			.to(FXDeleteSelectedOnTypePolicy.class);
	}

	@Override
	protected void bindIHandlePartFactory() {
		binder()
			.bind(new TypeLiteral<IHandlePartFactory<Node>>(){})
			.toInstance(new HandlePartFactory());
	}

	protected void bindFXDeleteHandlePartAdapters( MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
		adapterMapBinder
				.addBinding(AdapterKey.get(FXClickDragTool.CLICK_TOOL_POLICY_KEY, "delete"))
				.to(FXDeleteFirstAnchorageOnClickPolicy.class);
	}


	@Override
	protected void configure() {
		super.configure();

		binder()
			.bind(GlobalOnTypePolicy.class )
			.in(Scopes.SINGLETON);

		binder()
			.bind(new TypeLiteral<IContentPartFactory<Node>>(){})
			.toInstance(new ContentPartFactory());
		
		bindTextNodePartAdapters(
			AdapterMaps.getAdapterMapBinder(binder(), TextNodePart.class));
		
		bindFXDeleteHandlePartAdapters(
			AdapterMaps.getAdapterMapBinder(binder(), FXDeleteHoverHandlePart.class));

	}
}