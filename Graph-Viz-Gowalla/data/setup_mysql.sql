# We use group by to select the first checkin location of a user,
# thus we need to disable the ONLY_FULL_GROUP_BY mode
# reference: https://stackoverflow.com/questions/23921117/disable-only-full-group-by
SET GLOBAL sql_mode=(SELECT REPLACE(@@sql_mode,'ONLY_FULL_GROUP_BY',''));

DROP USER IF EXISTS 'user'@'localhost';
CREATE USER IF NOT EXISTS 'user'@'localhost' IDENTIFIED WITH mysql_native_password BY 'password';

create database IF NOT EXISTS gowalla;
use gowalla;

DROP TABLE IF EXISTS `friendships`;
CREATE TABLE `friendships` (
  `userA` int(10) unsigned NOT NULL,
  `userB` int(10) unsigned NOT NULL,
  KEY `userA` (`userA`,`userB`)
);

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `userId` int(10) unsigned NOT NULL,
  `checkInTime` datetime NOT NULL,
  `checkInLatitude` double NOT NULL,
  `checkInLongitude` double NOT NULL,
  `locationId` int(10) unsigned NOT NULL,
  KEY `userId` (`userId`)
);

GRANT SELECT ON gowalla.* TO 'user'@'localhost';

set GLOBAL local_infile=1;

load data local infile 'loc-gowalla_totalCheckins.txt' into TABLE users;

load data local infile 'loc-gowalla_edges.txt' into TABLE friendships;

DROP TABLE IF EXISTS `usersWithSingleCheckin`;
CREATE TABLE `usersWithSingleCheckin` (
  `userId` int(10) unsigned NOT NULL,
  `checkInTime` datetime NOT NULL,
  `checkInLatitude` double NOT NULL,
  `checkInLongitude` double NOT NULL,
  `locationId` int(10) unsigned NOT NULL,
  KEY `userId` (`userId`)
);
insert into usersWithSingleCheckin select * from users group by userId;
