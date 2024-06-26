CREATE TABLE log (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    Penalty VARCHAR(255),
    Date DATE,
    Against VARCHAR(255),
    Beneficiary VARCHAR(255),
    Player VARCHAR(255),
    Pos VARCHAR(50),
    Ref_Crew VARCHAR(255),
    Quarter INT,
    Time TIME,
    Down INT,
    Distance INT,
    Declined VARCHAR(255),
    Offsetting VARCHAR(255),
    Yards INT,
    Home VARCHAR(255),
    Phase VARCHAR(255),
    Week INT,
    Year INT
);

