function valid = check_TOWDECODEDstate(state)

    if bitand(state, 2^3)
        valid = 1;
    else
        valid = 0;
    end
end