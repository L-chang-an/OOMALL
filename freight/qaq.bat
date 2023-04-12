@echo off
set /a success=0
for /f "delims=, tokens=2-4 skip=1" %%A in (test%1.jtl) do (
    set /a re+=%%A
    set /a cnt+=1
    if %%C equ 200 (
       set /a success+=1
    ) else if %%C equ 201 (
       set /a success+=1
    )
)
set /a qaq=%re%/%cnt%
set /a tat=%re%%%%cnt%
if %cnt% equ 100 (
    echo cnt=%cnt%, success=%success%, elapsed avag=%qaq%.%tat%ms
) else (
    echo cnt=%cnt%, success=%success%, elapsed avag=%qaq%ms
)
del jmeter.log
del test%1.jtl
del test%1.jmx


