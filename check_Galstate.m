function E1C_2 = check_Galstate(state)

    if bitand(state, 2^11)
        E1C_2 = 1;
    else
        E1C_2 = 0;
    end
end