<div class="display-card">
	<ul class="card-page">
		<#if entries?has_content>
			<#list entries as entry>
				<li class="card-page-item card-page-item-asset">
					<div class="card card-type-asset file-card">
						<div class="aspect-ratio card-item-first">
							<div
								class="aspect-ratio-item aspect-ratio-item-center-middle aspect-ratio-item-fluid card-type-asset-icon">
								<#if validator.isNotNull(entry.getThumbnailURLString())>
									<img src="${entry.getThumbnailURLString()}" />
								<#else>
									<@clay.icon symbol="documents-and-media" />
								</#if>
							</div>
						</div>

						<div class="card-body">
							<div class="card-row">
								<div class="autofit-col autofit-col-expand">
									<section class="autofit-section">
										<span class="card-category text-truncate-inline">
											${entry.getCategoriesString()}
										</span>

										<h3 class="card-title" title="${entry.getTitle()}">
											<a href="${entry.getViewURL()}">
												${entry.getTitle()}
											</a>
										</h3>

										<p class="card-subtitle">
											<span class="text-truncate-inline">
												<span class="text-truncate">
													${entry.getCreatorUserName()} - ${entry.getCreationDateString()}
												</span>
											</span>
										</p>

										<p class="card-subtitle">
											<span class="text-truncate-inline">
												<span class="text-truncate">
													${entry.getModelResource()}
												</span>
											</span>
										</p>

										<div class="card-description">
											${entry.getContent()}
										</div>
									</section>
								</div>
							</div>
						</div>
					</div>
				</li>
			</#list>
		</#if>
	</ul>
</div>