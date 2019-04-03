import React, {Component} from 'react';
import AddResult from './AddResult.es';
import {PropTypes} from 'prop-types';
import ClayButton from '../ClayButton.es';
import Dropdown from './Dropdown.es';
import getCN from 'classnames';
import {getLang, sub} from 'utils/language.es';

class SearchBar extends Component {
	static propTypes = {
		/**
		 * The data map of id to object it represents. Search bar needs to know
		 * about the dataMap to determine which actions are allowed for the
		 * selected items.
		 */
		dataMap: PropTypes.object.isRequired,
		onAddResultSubmit: PropTypes.func,
		onClickHide: PropTypes.func,
		onClickPin: PropTypes.func,
		onSearchBarEnter: PropTypes.func,
		onSelectAll: PropTypes.func.isRequired,
		onSelectClear: PropTypes.func.isRequired,
		onUpdateSearchBarTerm: PropTypes.func,
		resultIds: PropTypes.arrayOf(String),
		searchBarTerm: PropTypes.string,
		selectedIds: PropTypes.arrayOf(String)
	};

	static defaultProps = {
		resultIds: [],
		selectedIds: []
	};

	selectAllCheckbox = React.createRef();

	/**
	 * Sets the indeterminate state of the select all checkbox.
	 */
	componentDidUpdate() {
		const {resultIds, selectedIds} = this.props;

		this.selectAllCheckbox.current.indeterminate =
			selectedIds.length > 0 && selectedIds.length !== resultIds.length;
	}

	_handleAllCheckbox = () => {
		this.props.selectedIds.length > 0
			? this.props.onSelectClear()
			: this.props.onSelectAll();
	};

	_handleClickHide = () => {
		this.props.onClickHide(this.props.selectedIds, !this._isAnyHidden());
	};

	_handleClickPin = () => {
		const {dataMap, onClickPin, onSelectClear, selectedIds} = this.props;

		const unpinnedIds = selectedIds.filter(id => !dataMap[id].pinned);

		if (unpinnedIds.length) {
			onClickPin(unpinnedIds, true);
		} else {
			onClickPin(selectedIds, false);
		}

		onSelectClear();
	};

	_handleSearchChange = event => {
		event.preventDefault();

		this.props.onUpdateSearchBarTerm(event.target.value);
	};

	_handleSearchEnter = () => {
		this.props.onSearchBarEnter();
	};

	_handleSearchKeyDown = event => {
		if (event.key === 'Enter') {
			this._handleSearchEnter();
		}
	};

	/**
	 * Checks if any selected ids contain any added items.
	 */
	_isAnyAddedResult = () => {
		const {dataMap, selectedIds} = this.props;

		return selectedIds.some(id => dataMap[id].addedResult);
	};

	/**
	 * Checks if any selected ids contain any hidden items.
	 */
	_isAnyHidden = () => {
		const {dataMap, selectedIds} = this.props;

		return selectedIds.some(id => dataMap[id].hidden);
	};

	/**
	 * Checks if any selected ids contain any unpinned items.
	 */
	_isAnyUnpinned = () => {
		const {dataMap, selectedIds} = this.props;

		return selectedIds.some(id => !dataMap[id].pinned);
	};

	render() {
		const {
			onAddResultSubmit,
			resultIds,
			searchBarTerm,
			selectedIds
		} = this.props;

		const classManagementBar = getCN(
			'management-bar',
			selectedIds.length > 0
				? 'management-bar-primary'
				: 'management-bar-light',
			'navbar',
			'navbar-expand-md'
		);

		return (
			<nav className={classManagementBar}>
				<div className="container-fluid container-fluid-max-xl">
					<div className="navbar-form navbar-form-autofit navbar-overlay navbar-overlay-sm-down">
						<ul className="navbar-nav">
							<li className="nav-item">
								<div className="custom-control custom-checkbox">
									<label>
										<input
											aria-label="Checkbox for search results"
											checked={selectedIds.length > 0}
											className="custom-control-input"
											onChange={this._handleAllCheckbox}
											ref={this.selectAllCheckbox}
											type="checkbox"
										/>

										<span className="custom-control-label" />
									</label>
								</div>
							</li>
						</ul>

						{selectedIds.length > 0
							? [
									<ul
										className="navbar-nav navbar-nav-expand"
										key="0"
									>
										<li className="nav-item">
											<span className="navbar-text">
												<strong>
													{sub(
														getLang(
															'x-of-x-items-selected'
														),
														[
															selectedIds.length,
															resultIds.length
														]
													)}
												</strong>
											</span>
										</li>
									</ul>,

									<ul className="navbar-nav" key="1">
										<li className="nav-item">
											{!this._isAnyAddedResult() && (
												<div className="nav-link nav-link-monospaced">
													<ClayButton
														borderless
														className="component-action"
														iconName="hidden"
														onClick={
															this
																._handleClickHide
														}
													/>
												</div>
											)}
										</li>

										<li className="nav-item">
											{!this._isAnyHidden() && (
												<div className="nav-link nav-link-monospaced">
													<ClayButton
														borderless
														className="component-action"
														iconName={
															this._isAnyUnpinned()
																? 'lock'
																: 'unlock'
														}
														onClick={
															this._handleClickPin
														}
													/>
												</div>
											)}
										</li>

										<li className="nav-item">
											<div className="nav-link nav-link-monospaced">
												<Dropdown
													hidden={this._isAnyHidden()}
													onClickHide={
														this._handleClickHide
													}
													onClickPin={
														this._handleClickPin
													}
													pinned={
														!this._isAnyUnpinned()
													}
													singular={
														selectedIds.length === 1
													}
												/>
											</div>
										</li>
									</ul>
							  ]
							: [
									<div
										className="navbar-nav navbar-nav-expand"
										key={0}
									>
										<div className="container-fluid container-fluid-max-xl">
											<div className="input-group">
												<div className="input-group-item">
													<input
														aria-label="Search for"
														className="form-control input-group-inset input-group-inset-after"
														onChange={
															this
																._handleSearchChange
														}
														onKeyDown={
															this
																._handleSearchKeyDown
														}
														placeholder={getLang(
															'contains-text'
														)}
														type="text"
														value={searchBarTerm}
													/>

													<div className="input-group-inset-item input-group-inset-item-after">
														<ClayButton
															displayStyle={
																'unstyled'
															}
															iconName="search"
															onClick={
																this
																	._handleSearchEnter
															}
														/>
													</div>
												</div>
											</div>
										</div>
									</div>,

									<div className="navbar-nav" key={1}>
										{onAddResultSubmit && (
											<AddResult
												onAddResultSubmit={
													onAddResultSubmit
												}
											/>
										)}
									</div>
							  ]}
					</div>
				</div>
			</nav>
		);
	}
}

export default SearchBar;
