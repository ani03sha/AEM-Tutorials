package org.redquark.aem.tutorials.core.services;

import com.day.cq.dam.api.Asset;

import java.util.Map;

public interface ReferencedAssetService {

    Map<String, Asset> getReferencedAssets(String pagePath);
}
