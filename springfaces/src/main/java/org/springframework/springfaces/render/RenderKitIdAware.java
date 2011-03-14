package org.springframework.springfaces.render;

import javax.faces.render.ResponseStateManager;

import org.springframework.springfaces.FacesWrapperFactory;

/**
 * Interface to be implemented by {@link ResponseStateManager}s that wish to be aware of the JSF <tt>renderKitId</tt>
 * being used. NOTE: Only {@link ResponseStateManager}s created from a {@link FacesWrapperFactory} will recieve this
 * callback.
 * 
 * @author Phillip Webb
 */
public interface RenderKitIdAware {
	/**
	 * Callback that supplies the <tt>renderKitId</tt>.
	 * @param renderKitId The render kit ID
	 */
	public void setRenderKitId(String renderKitId);
}