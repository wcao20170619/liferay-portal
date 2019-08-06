<div class="display-list">
	<ul class="list-group">
		<#if entries?has_content>
			<#list entries as entry>
				<li class="list-group-item list-group-item-flex">
					<div class="autofit-col">
						<div class="sticker sticker-secondary">
							<@clay.icon symbol="${entry.getIconId()}" />
						</div>
					</div>

					<div class="autofit-col autofit-col-expand">
						<section class="autofit-section">
							<div class="list-group-title">
								<a href="${entry.getViewURL()}" title="${entry.getTitle()}">
									${entry.getTitle()}
								</a>
							</div>

							<div class="related-results-metadata">
								<p class="list-group-subtext">
									${entry.getCreatorUserName()} - ${entry.getCreationDateString()}
								</p>

								<p class="list-group-subtext">
									${entry.getModelResource()}
								</p>
							</div>

							<p class="list-group-text related-results-description">
								${entry.getContent()}
							</p>
						</section>
					</div>
				</li>
			</#list>
		</#if>
	</ul>
</div>