function [Nobs,Obs_types,year,Rin_vers]  =   anheader(ObsFile)
%
%ANHEADER   Analyzes the header of an Observation RINEX 2.XX or 3.XX file 
%           with file name ObsFile and outputs the list of observation 
%           types (Obs_types), the number of observation types (Ntyp), the 
%           year of recording and the RINEX version.
%           If RINEX 2.XX:  Nobs is an integer
%                           Obs_types is a string
%           If RINEX 3.XX:  Nobs is a structure with an integer for each
%                           constellation
%                           Obs_types is a structure with a string for each
%                           constellation
%
%
fid         =   ObsFile;
Obs_types   =   cell(0,0);
%
while 1			   % Gobbling the header
   line     =   fgets(fid);   
   % end of file?
   if ~ischar(line)
       break; 
   end    
   % end of header?
   if ~isempty(strfind(line,'END OF HEADER'))
       break;                       % Exit function
   elseif ~isempty(strfind(line, 'TIME OF FIRST OBS'))
       % Get the utc time from the read line.
       utc              =   sscanf(line,'%f',6).';  
       year             =   utc(1); 
   elseif ~isempty(strfind(line, '# / TYPES OF OBSERV')) %RINEX v2.xx
       % Get the # of different observation types in the file.
       Rin_vers     =   2;                      % RINEX version.
       Ntyp         =   sscanf(line,'%f',1);
       % Get the different observation types in the file.
       ot           =   strtok(line(7:end),'#');
       Obs_types    =   sscanf(ot,'%s',9);
       % # of similar lines. (Only 9 types in each line)
       Nl           =   ceil(Ntyp/9);           
       if( Nl > 1 ) % Read&Write the rest of lines if Nl>1
           for ii = 2:Nl
               line         =   fgets(fid);         % Read a new line 
               ot           =   strtok(line(7:end),'#');
               Obs_types    =   [Obs_types sscanf(ot,'%s',9)];   
           end  
       end
       Nobs         =   Ntyp;
   elseif ~isempty(strfind(line,'SYS / # / OBS TYPES')) %RINEX v3.xx
       Rin_vers             =   3;                      % RINEX version.
       sysId                =   sscanf(line(1),'%s');   % one-letter constellation identifier.
       switch sysId
           case 'G'
               sysId        =   'GPS';
           case 'R'
               sysId        =   'GLO';
           case 'E'
               sysId        =   'GAL';
           case 'C'
               sysId        =   'CMP';
       end
       % Get the # of different observation types in the file.
       Ntyp                 =   sscanf(line(2:6),'%d');
       Nobs.(sysId)         =   Ntyp;
       % Definition of Obs_types struct for the constellation in sysId 
       Obs_types.(sysId) =   []; 
       nLinObs = ceil(Ntyp/13);
       for ii = 1 : nLinObs
           if (ii > 1)
               line = fgets(fid); % Read a new line
           end
           n = min(Ntyp,13);
           for k = 0 : n-1
               ot = sscanf(line(6+k*4+1:6+k*4+4),'%s');
               Obs_types.(sysId) = [Obs_types.(sysId) ot];
           end
           Ntyp = Ntyp - 13;
      end
   end
   
end
%%%%%%%%% end anheader.m %%%%%%%%%

%===============================================================================
% Format descriptions from 'rinex211.txt':
% 
%                                10 Dec 2007 (original)
%                        26-June 2012 (minor clarifications)
% 
% 
%          RINEX: The Receiver Independent Exchange Format Version 2.11
%          ************************************************************
% 
%                                 Werner Gurtner
%                             Astronomical Institute
%                              University of Berne
% 
%                                   Lou  Estey
%                                     UNAVCO
%                                  Boulder, Co.
%                                  
%                           IGS/RTCM RINEX Working Group
% 
%  +----------------------------------------------------------------------------+
%  |                                   TABLE A1                                 |
%  |          GNSS OBSERVATION DATA FILE - HEADER SECTION DESCRIPTION           |
%  +--------------------+------------------------------------------+------------+
%  |    HEADER LABEL    |               DESCRIPTION                |   FORMAT   |
%  |  (Columns 61-80)   |                                          |            |
%  +--------------------+------------------------------------------+------------+
%  |RINEX VERSION / TYPE| - Format version (2.11)                  | F9.2,11X,  |
%  |                    | - File type ('O' for Observation Data)   |   A1,19X,  |
%  |                    | - Satellite System: blank or 'G': GPS    |   A1,19X   |
%  |                    |                     'R': GLONASS         |            |
%  |                    |                     'S': Geostationary   |            |
%  |                    |                          signal payload  |            |
%  |                    |                     'E': Galileo         |            |
%  |                    |                     'M': Mixed           |            |
%  +--------------------+------------------------------------------+------------+
%  |PGM / RUN BY / DATE | - Name of program creating current file  |     A20,   |
%  |                    | - Name of agency  creating current file  |     A20,   |
%  |                    | - Date of file creation                  |     A20    |
%  +--------------------+------------------------------------------+------------+
% *|COMMENT             | Comment line(s)                          |     A60    |*
%  +--------------------+------------------------------------------+------------+
%  |MARKER NAME         | Name of antenna marker                   |     A60    |
%  +--------------------+------------------------------------------+------------+
% *|MARKER NUMBER       | Number of antenna marker                 |     A20    |*
%  +--------------------+------------------------------------------+------------+
%  |OBSERVER / AGENCY   | Name of observer / agency                |   A20,A40  |
%  +--------------------+------------------------------------------+------------+
%  |REC # / TYPE / VERS | Receiver number, type, and version       |    3A20    |
%  |                    | (Version: e.g. Internal Software Version)|            |
%  +--------------------+------------------------------------------+------------+
%  |ANT # / TYPE        | Antenna number and type                  |    2A20    |
%  +--------------------+------------------------------------------+------------+
%  |APPROX POSITION XYZ | Approximate marker position (WGS84)      |   3F14.4   |
%  +--------------------+------------------------------------------+------------+
%  |ANTENNA: DELTA H/E/N| - Antenna height: Height of bottom       |   3F14.4   |
%  |                    |   surface of antenna above marker        |            |
%  |                    | - Eccentricities of antenna center       |            |
%  |                    |   relative to marker to the east         |            |
%  |                    |   and north (all units in meters)        |            |
%  +--------------------+------------------------------------------+------------+
% *|WAVELENGTH FACT L1/2| - Default wavelength factors for         |            |*
%  |                    |   L1 and L2 (GPS only)                   |    2I6,    |
%  |                    |   1:  Full cycle ambiguities             |            |
%  |                    |   2:  Half cycle ambiguities (squaring)  |            |
%  |                    |   0 (in L2): Single frequency instrument |            |
%  |                    | - zero or blank                          |     I6     |
%  |                    |                                          |            |
%  |                    | The wavelength factor record is optional |            |
%  |                    | for GPS and obsolete for other systems.  |            |
%  |                    | Wavelength factors default to 1.         |            |
%  |                    | If the record exists it must precede any |            |
%  |                    | satellite-specific records (see below).  |            |
%  +--------------------+------------------------------------------+------------+
% *|WAVELENGTH FACT L1/2| - Wavelength factors for L1 and L2 (GPS) |    2I6,    |*
%  |                    |   1:  Full cycle ambiguities             |            |
%  |                    |   2:  Half cycle ambiguities (squaring)  |            |
%  |                    |   0 (in L2): Single frequency instrument |            |
%  |                    | - Number of satellites to follow in list |     I6,    |
%  |                    |   for which these factors are valid.     |            |
%  |                    | - List of PRNs (satellite numbers with   | 7(3X,A1,I2)|
%  |                    |   system identifier)                     |            |
%  |                    |                                          |            |
%  |                    | These optional satellite specific lines  |            |
%  |                    | may follow, if they identify a state     |            |
%  |                    | different from the default values.       |            |
%  |                    |                                          |            |
%  |                    | Repeat record if necessary.              |            |
%  +--------------------+------------------------------------------+------------+
%  |# / TYPES OF OBSERV | - Number of different observation types  |     I6,    |
%  |                    |   stored in the file                     |            |
%  |                    | - Observation types                      |            |
%  |                    |   - Observation code                     | 9(4X,A1,   |
%  |                    |   - Frequency code                       |         A1)|
%  |                    |   If more than 9 observation types:      |            |
%  |                    |     Use continuation line(s) (including  |6X,9(4X,2A1)|
%  |                    |     the header label in cols. 61-80!)    |            |
%  |                    |                                          |            |
%  |                    | The following observation types are      |            |
%  |                    | defined in RINEX Version 2.11:           |            |
%  |                    |                                          |            |
%  |                    | Observation code (use uppercase only):   |            |
%  |                    |   C: Pseudorange  GPS: C/A, L2C          |            |
%  |                    |                   Glonass: C/A           |            |
%  |                    |                   Galileo: All           |            |
%  |                    |   P: Pseudorange  GPS and Glonass: P code|            |
%  |                    |   L: Carrier phase                       |            |
%  |                    |   D: Doppler frequency                   |            |
%  |                    |   S: Raw signal strengths or SNR values  |            |
%  |                    |      as given by the receiver for the    |            |
%  |                    |      respective phase observations       |            |
%  |                    |                                          |            |
%  |                    | Frequency code                           |            |
%  |                    |      GPS    Glonass   Galileo    SBAS    |            |
%  |                    |   1:  L1       G1     E2-L1-E1    L1     |            |
%  |                    |   2:  L2       G2        --       --     |            |
%  |                    |   5:  L5       --        E5a      L5     |            |
%  |                    |   6:  --       --        E6       --     |            |
%  |                    |   7:  --       --        E5b      --     |            |
%  |                    |   8:  --       --       E5a+b     --     |            |
%  |                    |                                          |            |
%  |                    | Observations collected under Antispoofing|            |
%  |                    | are converted to "L2" or "P2" and flagged|            |
%  |                    | with bit 2 of loss of lock indicator     |            |
%  |                    | (see Table A2).                          |            |
%  |                    |                                          |            |
%  |                    | Units : Phase       : full cycles        |            |
%  |                    |         Pseudorange : meters             |            |
%  |                    |         Doppler     : Hz                 |            |
%  |                    |         SNR etc     : receiver-dependent |            |
%  |                    |                                          |            |
%  |                    | The sequence of the types in this record |            |
%  |                    | has to correspond to the sequence of the |            |
%  |                    | observations in the observation records  |            |
%  +--------------------+------------------------------------------+------------+
% *|INTERVAL            | Observation interval in seconds          |   F10.3    |*
%  +--------------------+------------------------------------------+------------+
%  |TIME OF FIRST OBS   | - Time of first observation record       | 5I6,F13.7, |
%  |                    |   (4-digit-year, month,day,hour,min,sec) |            |
%  |                    | - Time system: GPS (=GPS time system)    |   5X,A3    |
%  |                    |                GLO (=UTC time system)    |            |
%  |                    |                GAL (=Galileo System Time)|            |
%  |                    |   Compulsory in mixed GPS/GLONASS files  |            |
%  |                    |   Defaults: GPS for pure GPS files       |            |
%  |                    |             GLO for pure GLONASS files   |            |
%  |                    |             GAL for pure Galileo files   |            |
%  +--------------------+------------------------------------------+------------+
% *|TIME OF LAST OBS    | - Time of last  observation record       | 5I6,F13.7, |*
%  |                    |   (4-digit-year, month,day,hour,min,sec) |            |
%  |                    | - Time system: Same value as in          |   5X,A3    |
%  |                    |                TIME OF FIRST OBS record  |            |
%  +--------------------+------------------------------------------+------------+
% *|RCV CLOCK OFFS APPL | Epoch, code, and phase are corrected by  |     I6     |*
%  |                    | applying the realtime-derived receiver   |            |
%  |                    | clock offset: 1=yes, 0=no; default: 0=no |            |
%  |                    | Record required if clock offsets are     |            |
%  |                    | reported in the EPOCH/SAT records        |            |
%  +--------------------+------------------------------------------+------------+
% *|LEAP SECONDS        | Number of leap seconds since 6-Jan-1980  |     I6     |*
%  |                    | Recommended for mixed files              |            |
%  +--------------------+------------------------------------------+------------+
% *|# OF SATELLITES     | Number of satellites, for which          |     I6     |*
%  |                    | observations are stored in the file      |            |
%  +--------------------+------------------------------------------+------------+
% *|PRN / # OF OBS      | PRN (sat.number), number of observations |3X,A1,I2,9I6|*
%  |                    | for each observation type indicated      |            |
%  |                    | in the "# / TYPES OF OBSERV" - record.   |            |
%  |                    |                                          |            |
%  |                    |   If more than 9 observation types:      |            |
%  |                    |   Use continuation line(s) including     |   6X,9I6   |
%  |                    |   the header label in cols. 61-80!       |            |
%  |                    |                                          |            |
%  |                    | This record is (these records are)       |            |
%  |                    | repeated for each satellite present in   |            |
%  |                    | the data file                            |            |
%  +--------------------+------------------------------------------+------------+
%  |END OF HEADER       | Last record in the header section.       |    60X     |
%  +--------------------+------------------------------------------+------------+
% 
%                         Records marked with * are optional
% 
% 
%  +----------------------------------------------------------------------------+
%  |                                   TABLE A2                                 |
%  |            GNSS OBSERVATION DATA FILE - DATA RECORD DESCRIPTION            |
%  +-------------+-------------------------------------------------+------------+
%  | OBS. RECORD | DESCRIPTION                                     |   FORMAT   |
%  +-------------+-------------------------------------------------+------------+
%  | EPOCH/SAT   | - Epoch :                                       |            |
%  |     or      |   - year (2 digits, padded with 0 if necessary) |  1X,I2.2,  |
%  | EVENT FLAG  |   - month,day,hour,min,                         |  4(1X,I2), |
%  |             |   - sec                                         |   F11.7,   |
%  |             |                                                 |            |
%  |             | - Epoch flag 0: OK                              |   2X,I1,   |
%  |             |              1: power failure between           |            |
%  |             |                 previous and current epoch      |            |
%  |             |             >1: Event flag                      |            |
%  |             | - Number of satellites in current epoch         |     I3,    |
%  |             | - List of PRNs (sat.numbers with system         | 12(A1,I2), |
%  |             |   identifier, see 5.1) in current epoch         |            |
%  |             | - receiver clock offset (seconds, optional)     |   F12.9    |
%  |             |                                                 |            |
%  |             |   If more than 12 satellites: Use continuation  |    32X,    |
%  |             |   line(s)                                       | 12(A1,I2)  |
%  |             |                                                 |            |
%  |             | If epoch flag  2-5:                             |            |
%  |             |                                                 |            |
%  |             |   - Event flag:                                 |  [2X,I1,]  |
%  |             |     2: start moving antenna                     |            |
%  |             |     3: new site occupation (end of kinem. data) |            |
%  |             |        (at least MARKER NAME record follows)    |            |
%  |             |     4: header information follows               |            |
%  |             |     5: external event (epoch is significant,    |            |
%  |             |        same time frame as observation time tags)|            |
%  |             |                                                 |            |
%  |             |   - "Number of satellites" contains number of   |    [I3]    |
%  |             |     special records to follow.                  |            |
%  |             |     Maximum number of records: 999              |            |
%  |             |                                                 |            |
%  |             |   - For events without significant epoch the    |            |
%  |             |     epoch fields can be left blank              |            |
%  |             |                                                 |            |
%  |             | If epoch flag = 6:                              |            |
%  |             |     6: cycle slip records follow to optionally  |            |
%  |             |        report detected and repaired cycle slips |            |
%  |             |        (same format as OBSERVATIONS records;    |            |
%  |             |         slip instead of observation; LLI and    |            |
%  |             |         signal strength blank or zero)          |            |
%  +-------------+-------------------------------------------------+------------+
%  |OBSERVATIONS | - Observation      | rep. within record for     |  m(F14.3,  |
%  |             | - LLI              | each obs.type (same seq    |     I1,    |
%  |             | - Signal strength  | as given in header)        |     I1)    |
%  |             |                                                 |            |
%  |             | If more than 5 observation types (=80 char):    |            |
%  |             | continue observations in next record.           |            |
%  |             |                                                 |            |
%  |             | This record is (these records are) repeated for |            |
%  |             | each satellite given in EPOCH/SAT - record.     |            |
%  |             |                                                 |            |
%  |             | Observations:                                   |            |
%  |             |   Phase  : Units in whole cycles of carrier     |            |
%  |             |   Code   : Units in meters                      |            |
%  |             | Missing observations are written as 0.0         |            |
%  |             | or blanks.                                      |            |
%  |             |                                                 |            |
%  |             | Phase values overflowing the fixed format F14.3 |            |
%  |             | have to be clipped into the valid interval (e.g.|            |
%  |             | add or subtract 10**9), set LLI indicator.      |            |
%  |             |                                                 |            |
%  |             | Loss of lock indicator (LLI). Range: 0-7        |            |
%  |             |  0 or blank: OK or not known                    |            |
%  |             |  Bit 0 set : Lost lock between previous and     |            |
%  |             |              current observation: cycle slip    |            |
%  |             |              possible                           |            |
%  |             |  Bit 1 set : Opposite wavelength factor to the  |            |
%  |             |              one defined for the satellite by a |            |
%  |             |              previous WAVELENGTH FACT L1/2 line |            |
%  |             |              or opposite to the default.        |            |
%  |             |              Valid for the current epoch only.  |            |
%  |             |  Bit 2 set : Observation under Antispoofing     |            |
%  |             |              (may suffer from increased noise)  |            |
%  |             |                                                 |            |
%  |             |  Bits 0 and 1 for phase only.                   |            |
%  |             |                                                 |            |
%  |             | Signal strength projected into interval 1-9:    |            |
%  |             |  1: minimum possible signal strength            |            |
%  |             |  5: threshold for good S/N ratio                |            |
%  |             |  9: maximum possible signal strength            |            |
%  |             |  0 or blank: not known, don't care              |            |
%  +-------------+-------------------------------------------------+------------+
%===============================================================================








