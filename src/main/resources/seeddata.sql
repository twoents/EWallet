insert into ew_user ( username, passhash )
values ( 'system', 'helloSystem' );

insert into ew_wallet ( wal_name, balance, user_id )
values ( 'main', 0.00, ( select id from ew_user where username = 'system' ) );
 

