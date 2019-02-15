create table oso_outsourcing_order (
	id binary(16) not null,
	canceled_date datetime,
	charger_id varchar(50),
	code varchar(20),
	created_by_id varchar(50),
	created_by_name varchar(50),
	created_date datetime,
	determined_date datetime,
	due_date datetime,
	last_modified_by_id varchar(50),
	last_modified_by_name varchar(50),
	last_modified_date datetime,
	receive_address_detail varchar(50),
	receive_address_postal_code varchar(10),
	receive_address_street varchar(50),
	received_date datetime,
	receiver_id varchar(50),
	rejected_date datetime,
	rejected_reason varchar(50),
	remark varchar(50),
	sent_date datetime,
	status varchar(20),
	supplier_id varchar(50),
	primary key (id)
) engine=InnoDB;

create table oso_outsourcing_order_item (
	id binary(16) not null,
	created_by_id varchar(50),
	created_by_name varchar(50),
	created_date datetime,
	estimated_unit_cost decimal(19,2),
	item_id binary(16),
	item_spec_code varchar(20),
	last_modified_by_id varchar(50),
	last_modified_by_name varchar(50),
	last_modified_date datetime,
	order_id binary(16),
	process_id binary(16),
	project_id binary(16),
	quantity decimal(19,2),
	received_quantity decimal(19,2),
	remark varchar(50),
	request_id binary(16),
	spare_quantity decimal(19,2),
	status varchar(20),
	unit varchar(20),
	unit_cost decimal(19,2),
	primary key (id)
) engine=InnoDB;

create table oso_outsourcing_order_material (
	id binary(16) not null,
	created_by_id varchar(50),
	created_by_name varchar(50),
	created_date datetime,
	estimated_supply_date datetime,
	item_id binary(16),
	item_spec_code varchar(20),
	last_modified_by_id varchar(50),
	last_modified_by_name varchar(50),
	last_modified_date datetime,
	order_id binary(16),
	quantity decimal(19,2),
	remark varchar(50),
	supplier_id varchar(50),
	unit varchar(20),
	primary key (id)
) engine=InnoDB;

create index IDXscjeoduksjdbdnqhsx7utd5gg
	on oso_outsourcing_order_item (order_id);

create index IDXcwkpihw9d0wn4bd5g22mvqwgg
	on oso_outsourcing_order_item (request_id);

create index IDXsthkj1k032nya5tpfjygbxcy9
	on oso_outsourcing_order_material (order_id);
