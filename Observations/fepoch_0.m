function [tow,Nsat,sats,eof,line] = fepoch_0(fid,year)
% FEPOCH_0   Finds the next epoch in an opened RINEX file with
%	          identification fid. From the epoch line is produced
%	          time (in seconds of week), number of sv.s, and a mark
%	          about end of file. Only observations with epoch flag 0
%	          are delt with. If RINEX 2.XX the function outputs the list of
%	          satellites in the given epoch.
%

eof = 0;
sats    =   [];

while 1
    if (feof(fid) == 1)
      eof = 1;
      break
    end
    lin      =   fgets(fid); 
    if ~ischar(lin)
        eof = 1;
        break
    end
    a       =   regexp(lin,'[GRE]\d\d','once'); % for RINEX 2.XX
    b       =   strfind(lin,'>');               % for RINEX 3.XX
    %    We only want type 0 data
    if (~isempty(a) || ~isempty(b) )
       if( (strcmp(lin(29),'0') == 1 && ~isempty(a)) || (strcmp(lin(32),'0') == 1 && ~isempty(b)) )
           line     =   lin;
           if( ~isempty(b) )    % RINEX 3.XX
              utc      =   sscanf(lin(2:end),'%f',6).';
              Nsat     =   sscanf(lin(33:end),'%f');
              utc(1)   =   year;
           else                 % RINEX 2.XX
               utc      =   sscanf(lin,'%f',6).';
               utc(1)   =   year;
               s        =   sscanf(lin(1:a-1),'%f');
               Nsat     =   s(end);
           end
           %-   Get GPS time
           tow          =   utc2gpstow(utc,0);
           
           if( ~isempty(a) )
               if ispc
                   sats =   lin(a:end-1);
                    for jj = 2:ceil(Nsat/12) % More than 1 line in the Rinex file with SVN (12 per line)
                        lin      =   fgets(fidi);
                        sats     =   [sats sscanf(lin,'%s')];
                    end
                   
               else
                   sats =   lin(a:end-2);
                   for jj = 2:ceil(Nsat/12) % More than 1 line in the Rinex file with SVN (12 per line)
                        lin      =   fgets(fidi);
                        sats     =   [sats sscanf(lin,'%s')];
                    end
               end
           end

           break;
           
       else
           error(['non-type 0 data line at line: ' lin '; See!!']);
       end
   end  
   
end

%%%%%%%% end fepoch_0.m %%%%%%%%%
