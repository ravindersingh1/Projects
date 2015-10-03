create table users
	(
	user_name varchar(30) primary key,
	password varchar(30) NOT NULL,
	login_flag TINYINT(1) NOT NULL
	);

create table friends
	( 
	user varchar(30) NOT NULL, 
	friend varchar(30) NOT NULL, 
	foreign key (user) references users(User_name), 
	foreign key (friend) references users(User_name)
	);

insert into users values ('Cathy', 'cathy', 0);
insert into users values ('Michelle', 'michelle', 0);
insert into users values ('Sibe', 'sibe', 0);
insert into users values ('Toni', 'Toni', 0);
insert into users values ('Rahul', 'rahul', 0);
insert into users values ('Sammy', 'sammy', 0);
insert into users values ('Ron', 'ron', 0);


insert into friends values('Toni','Sibe');
insert into friends values('Michelle','Sibe');
insert into friends values('Cathy','Toni');
insert into friends values('Rahul','Cathy');
insert into friends values('Toni','Cathy');
insert into friends values('Cathy','Sibe');
insert into friends values('Cathy','Rahul');
insert into friends values('Sibe','Toni');
insert into friends values('Sibe','Michelle');
insert into friends values('Sibe','Cathy');

commit;


