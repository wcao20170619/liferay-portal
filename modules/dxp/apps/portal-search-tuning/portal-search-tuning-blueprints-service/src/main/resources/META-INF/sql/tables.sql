create table Blueprint (
	mvccVersion LONG default 0 not null,
	uuid_ VARCHAR(75) null,
	blueprintId LONG not null primary key,
	groupId LONG,
	companyId LONG,
	userId LONG,
	userName VARCHAR(75) null,
	createDate DATE null,
	modifiedDate DATE null,
	status INTEGER,
	statusByUserId LONG,
	statusByUserName VARCHAR(75) null,
	statusDate DATE null,
	title STRING null,
	description STRING null,
	configuration TEXT null,
	type_ INTEGER
);