CREATE TABLE players (
  player_id INT AUTO_INCREMENT PRIMARY KEY,
  Name VARCHAR(255),
  Team VARCHAR(255),
  Pos VARCHAR(50),
  Count INT,
  Yards INT,
  Declined INT,
  Offsetting INT,  
  Pre_Snap INT,
  Penalties VARCHAR(255),
  Week INT,
  Year INT
);

