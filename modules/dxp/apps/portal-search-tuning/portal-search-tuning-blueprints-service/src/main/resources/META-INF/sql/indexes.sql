create index IX_4B14110F on Blueprint (companyId);
create index IX_3FDE80BE on Blueprint (groupId, status, type_);
create index IX_DE92D64 on Blueprint (groupId, type_);
create index IX_3BEA732D on Blueprint (uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_6DDFE0EF on Blueprint (uuid_[$COLUMN_LENGTH:75$], groupId);