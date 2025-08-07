#!/usr/bin/env python3
"""
Financial Advisor Excel Generator - advisor_sri.xlsx
Creates a comprehensive financial tracking and goal planning workbook
"""

import pandas as pd
import numpy as np
from datetime import datetime, timedelta
import xlsxwriter
from xlsxwriter.utility import xl_rowcol_to_cell

def create_financial_advisor_workbook():
    """Create comprehensive financial advisor Excel workbook"""
    
    # Create workbook
    workbook = xlsxwriter.Workbook('/Workshop/ESG_Platform_v1/workdir/advisor_sri.xlsx')
    
    # Define formats
    header_format = workbook.add_format({
        'bold': True,
        'font_size': 12,
        'bg_color': '#4472C4',
        'font_color': 'white',
        'align': 'center',
        'valign': 'vcenter',
        'border': 1
    })
    
    currency_format = workbook.add_format({
        'num_format': '₹#,##0',
        'align': 'right',
        'border': 1
    })
    
    percentage_format = workbook.add_format({
        'num_format': '0.00%',
        'align': 'right',
        'border': 1
    })
    
    date_format = workbook.add_format({
        'num_format': 'dd-mmm-yyyy',
        'align': 'center',
        'border': 1
    })
    
    cell_format = workbook.add_format({
        'align': 'left',
        'border': 1
    })
    
    total_format = workbook.add_format({
        'bold': True,
        'num_format': '₹#,##0',
        'bg_color': '#D9E1F2',
        'border': 1
    })

    # Navigation button format
    nav_format = workbook.add_format({
        'bold': True,
        'font_size': 10,
        'bg_color': '#70AD47',
        'font_color': 'white',
        'align': 'center',
        'valign': 'vcenter',
        'border': 2
    })
    
    # 1. DASHBOARD SHEET
    dashboard = workbook.add_worksheet('Dashboard')
    dashboard.set_column('A:A', 20)
    dashboard.set_column('B:B', 15)
    dashboard.set_column('C:C', 15)
    dashboard.set_column('D:D', 15)
    dashboard.set_column('E:E', 15)
    
    # Dashboard headers
    dashboard.write('A1', 'FINANCIAL DASHBOARD', header_format)
    dashboard.merge_range('A1:E1', 'FINANCIAL DASHBOARD', header_format)
    
    # Navigation buttons
    dashboard.write('A2', 'Go to Income', nav_format)
    dashboard.write('B2', 'Go to Expenses', nav_format) 
    dashboard.write('C2', 'Go to Assets', nav_format)
    dashboard.write('D2', 'Go to Liabilities', nav_format)
    dashboard.write('E2', 'Go to Goals', nav_format)
    
    # Net Worth Summary
    dashboard.write('A4', 'NET WORTH SUMMARY', header_format)
    dashboard.merge_range('A4:B4', 'NET WORTH SUMMARY', header_format)
    dashboard.write('A5', 'Total Assets', cell_format)
    dashboard.write('B5', '=Assets!B20', currency_format)
    dashboard.write('C5', '→ View Assets', nav_format)
    dashboard.write('A6', 'Total Liabilities', cell_format)
    dashboard.write('B6', '=Liabilities!B15', currency_format)
    dashboard.write('C6', '→ View Liabilities', nav_format)
    dashboard.write('A7', 'Net Worth', total_format)
    dashboard.write('B7', '=B5-B6', total_format)
    
    # Monthly Cash Flow
    dashboard.write('A9', 'MONTHLY CASH FLOW', header_format)
    dashboard.merge_range('A9:B9', 'MONTHLY CASH FLOW', header_format)
    dashboard.write('A10', 'Total Income', cell_format)
    dashboard.write('B10', '=Income!B15', currency_format)
    dashboard.write('C10', '→ View Income', nav_format)
    dashboard.write('A11', 'Total Expenses', cell_format)
    dashboard.write('B11', '=Expenses!B25', currency_format)
    dashboard.write('C11', '→ View Expenses', nav_format)
    dashboard.write('A12', 'Monthly Surplus', total_format)
    dashboard.write('B12', '=B10-B11', total_format)
    
    # Goal Progress
    dashboard.write('A14', 'GOAL PROGRESS', header_format)
    dashboard.merge_range('A14:B14', 'GOAL PROGRESS', header_format)
    dashboard.write('A15', 'Goals Defined', cell_format)
    dashboard.write('B15', '=COUNTA(Goals!A3:A12)', cell_format)
    dashboard.write('C15', '→ View Goals', nav_format)
    dashboard.write('A16', 'Total Goal Amount', cell_format)
    dashboard.write('B16', '=SUM(Goals!D3:D12)', currency_format)
    
    # Quick Ratios
    dashboard.write('A18', 'FINANCIAL RATIOS', header_format)
    dashboard.merge_range('A18:B18', 'FINANCIAL RATIOS', header_format)
    dashboard.write('A19', 'Debt-to-Income Ratio', cell_format)
    dashboard.write('B19', '=B6/B10', percentage_format)
    dashboard.write('A20', 'Savings Rate', cell_format)
    dashboard.write('B20', '=B12/B10', percentage_format)
    dashboard.write('A21', 'Asset-to-Liability Ratio', cell_format)
    dashboard.write('B21', '=B5/B6', cell_format)

    # 2. INCOME SHEET
    income = workbook.add_worksheet('Income')
    income.set_column('A:A', 25)
    income.set_column('B:B', 15)
    income.set_column('C:C', 15)
    
    income.write('A1', 'MONTHLY INCOME TRACKER', header_format)
    income.merge_range('A1:C1', 'MONTHLY INCOME TRACKER', header_format)
    
    # Navigation back to dashboard
    income.write('A2', '← Back to Dashboard', nav_format)
    income.write('B2', 'View Expenses →', nav_format)
    income.write('C2', 'View Assets →', nav_format)
    
    income_categories = [
        'Salary/Wages', 'Business Income', 'Rental Income', 'Dividend Income',
        'Interest Income', 'Capital Gains', 'Freelance Income', 'Pension',
        'Social Security', 'Other Income 1', 'Other Income 2', 'Other Income 3'
    ]
    
    income.write('A4', 'Income Source', header_format)
    income.write('B4', 'Monthly Amount (₹)', header_format)
    
    for i, category in enumerate(income_categories, 5):
        income.write(f'A{i}', category, cell_format)
        income.write(f'B{i}', 0, currency_format)
    
    income.write('A17', 'TOTAL MONTHLY INCOME', total_format)
    income.write('B17', f'=SUM(B5:B16)', total_format)
    
    # Link to dashboard
    income.write('A19', 'This total appears in Dashboard B10', cell_format)
    income.write('A20', 'Monthly Surplus = Income - Expenses', cell_format)
    income.write('B20', f'=B17-Expenses!B27', currency_format)

    # 3. EXPENSES SHEET
    expenses = workbook.add_worksheet('Expenses')
    expenses.set_column('A:A', 25)
    expenses.set_column('B:B', 15)
    expenses.set_column('C:C', 15)
    
    expenses.write('A1', 'MONTHLY EXPENSE TRACKER', header_format)
    expenses.merge_range('A1:C1', 'MONTHLY EXPENSE TRACKER', header_format)
    
    # Navigation
    expenses.write('A2', '← Back to Dashboard', nav_format)
    expenses.write('B2', '← View Income', nav_format)
    expenses.write('C2', 'View Liabilities →', nav_format)
    
    expense_categories = [
        'Housing (Rent/EMI)', 'Utilities', 'Groceries', 'Transportation',
        'Insurance Premiums', 'Healthcare', 'Education', 'Entertainment',
        'Dining Out', 'Clothing', 'Personal Care', 'Phone/Internet',
        'Investments/SIP', 'Loan Payments', 'Credit Card Payments',
        'Emergency Fund', 'Charity/Donations', 'Travel', 'Hobbies',
        'Maintenance', 'Other Expenses 1', 'Other Expenses 2'
    ]
    
    expenses.write('A4', 'Expense Category', header_format)
    expenses.write('B4', 'Monthly Amount (₹)', header_format)
    
    for i, category in enumerate(expense_categories, 5):
        expenses.write(f'A{i}', category, cell_format)
        expenses.write(f'B{i}', 0, currency_format)
    
    expenses.write('A27', 'TOTAL MONTHLY EXPENSES', total_format)
    expenses.write('B27', f'=SUM(B5:B26)', total_format)
    
    # Link to other sheets
    expenses.write('A29', 'This total appears in Dashboard B11', cell_format)
    expenses.write('A30', 'Available for Investment', cell_format)
    expenses.write('B30', f'=Income!B17-B27', currency_format)
    expenses.write('A31', 'Expense-to-Income Ratio', cell_format)
    expenses.write('B31', f'=B27/Income!B17', percentage_format)

    # 4. ASSETS SHEET
    assets = workbook.add_worksheet('Assets')
    assets.set_column('A:A', 25)
    assets.set_column('B:B', 15)
    assets.set_column('C:C', 15)
    
    assets.write('A1', 'ASSET PORTFOLIO', header_format)
    assets.merge_range('A1:C1', 'ASSET PORTFOLIO', header_format)
    
    # Navigation
    assets.write('A2', '← Back to Dashboard', nav_format)
    assets.write('B2', '← View Income', nav_format)
    assets.write('C2', 'View Liabilities →', nav_format)
    
    asset_categories = [
        'Cash in Hand', 'Savings Account', 'Current Account', 'Fixed Deposits',
        'Mutual Funds', 'Stocks/Equity', 'Bonds', 'PPF', 'EPF', 'NPS',
        'Real Estate (Primary)', 'Real Estate (Investment)', 'Gold/Jewelry',
        'Vehicle', 'Insurance (Cash Value)', 'Business Assets', 'Other Assets 1', 'Other Assets 2'
    ]
    
    assets.write('A4', 'Asset Type', header_format)
    assets.write('B4', 'Current Value (₹)', header_format)
    
    for i, category in enumerate(asset_categories, 5):
        assets.write(f'A{i}', category, cell_format)
        assets.write(f'B{i}', 0, currency_format)
    
    assets.write('A23', 'TOTAL ASSETS', total_format)
    assets.write('B23', f'=SUM(B5:B22)', total_format)
    
    # Link to dashboard and liabilities
    assets.write('A25', 'This total appears in Dashboard B5', cell_format)
    assets.write('A26', 'Net Worth = Assets - Liabilities', cell_format)
    assets.write('B26', f'=B23-Liabilities!B17', currency_format)
    assets.write('A27', 'Asset Growth Target (10% annually)', cell_format)
    assets.write('B27', f'=B23*1.1', currency_format)

    # 5. LIABILITIES SHEET
    liabilities = workbook.add_worksheet('Liabilities')
    liabilities.set_column('A:A', 25)
    liabilities.set_column('B:B', 15)
    liabilities.set_column('C:C', 15)
    
    liabilities.write('A1', 'LIABILITY PORTFOLIO', header_format)
    liabilities.merge_range('A1:C1', 'LIABILITY PORTFOLIO', header_format)
    
    # Navigation
    liabilities.write('A2', '← Back to Dashboard', nav_format)
    liabilities.write('B2', '← View Assets', nav_format)
    liabilities.write('C2', 'View Expenses →', nav_format)
    
    liability_categories = [
        'Home Loan', 'Car Loan', 'Personal Loan', 'Education Loan',
        'Credit Card Outstanding', 'Business Loan', 'Gold Loan',
        'Loan from Friends/Family', 'Other Loans 1', 'Other Loans 2',
        'Outstanding Bills', 'Tax Liabilities'
    ]
    
    liabilities.write('A4', 'Liability Type', header_format)
    liabilities.write('B4', 'Outstanding Amount (₹)', header_format)
    
    for i, category in enumerate(liability_categories, 5):
        liabilities.write(f'A{i}', category, cell_format)
        liabilities.write(f'B{i}', 0, currency_format)
    
    liabilities.write('A17', 'TOTAL LIABILITIES', total_format)
    liabilities.write('B17', f'=SUM(B5:B16)', total_format)
    
    # Link to other sheets
    liabilities.write('A19', 'This total appears in Dashboard B6', cell_format)
    liabilities.write('A20', 'Net Worth = Assets - Liabilities', cell_format)
    liabilities.write('B20', f'=Assets!B23-B17', currency_format)
    liabilities.write('A21', 'Debt-to-Asset Ratio', cell_format)
    liabilities.write('B21', f'=B17/Assets!B23', percentage_format)
    liabilities.write('A22', 'Monthly Debt Service (Est. 10%)', cell_format)
    liabilities.write('B22', f'=B17*0.1/12', currency_format)

    # 6. GOALS SHEET
    goals = workbook.add_worksheet('Goals')
    goals.set_column('A:A', 20)
    goals.set_column('B:B', 12)
    goals.set_column('C:C', 12)
    goals.set_column('D:D', 15)
    goals.set_column('E:E', 12)
    goals.set_column('F:F', 15)
    goals.set_column('G:G', 15)
    goals.set_column('H:H', 15)
    
    goals.write('A1', 'FINANCIAL GOALS PLANNER', header_format)
    goals.merge_range('A1:H1', 'FINANCIAL GOALS PLANNER', header_format)
    
    # Headers
    headers = ['Goal Name', 'Years to Goal', 'Inflation %', 'Future Value Needed (₹)', 
               'Current Savings (₹)', 'Monthly SIP Required (₹)', 'Expected Return %', 'Status']
    
    for i, header in enumerate(headers):
        goals.write(1, i, header, header_format)
    
    # Sample goals with formulas
    sample_goals = [
        'Child Education', 'Child Marriage', 'House Purchase', 'Car Purchase',
        'Retirement Fund', 'Emergency Fund', 'Vacation Fund', 'Business Setup',
        'Health Insurance', 'Other Goal'
    ]
    
    for i, goal in enumerate(sample_goals, 2):
        goals.write(f'A{i+1}', goal, cell_format)
        goals.write(f'B{i+1}', 10, cell_format)  # Default 10 years
        goals.write(f'C{i+1}', 0.06, percentage_format)  # 6% inflation
        goals.write(f'D{i+1}', 1000000, currency_format)  # 10 lakh default
        goals.write(f'E{i+1}', 0, currency_format)  # Current savings
        
        # Monthly SIP calculation: PMT formula
        # =PMT(G3/12, B3*12, -E3, D3)
        goals.write(f'F{i+1}', f'=PMT(G{i+1}/12,B{i+1}*12,-E{i+1},D{i+1})', currency_format)
        goals.write(f'G{i+1}', 0.12, percentage_format)  # 12% expected return
        goals.write(f'H{i+1}', 'Planning', cell_format)

    # 7. CALCULATIONS SHEET
    calc = workbook.add_worksheet('Calculations')
    calc.set_column('A:A', 30)
    calc.set_column('B:B', 20)
    calc.set_column('C:C', 15)
    
    calc.write('A1', 'FINANCIAL CALCULATIONS & FORMULAS', header_format)
    calc.merge_range('A1:C1', 'FINANCIAL CALCULATIONS & FORMULAS', header_format)
    
    # Key formulas explanation
    calc.write('A3', 'Future Value with Inflation:', cell_format)
    calc.write('B3', 'FV = PV × (1 + inflation)^years', cell_format)
    
    calc.write('A4', 'Monthly SIP for Goal:', cell_format)
    calc.write('B4', 'PMT(rate/12, years×12, -current, future)', cell_format)
    
    calc.write('A5', 'Compound Annual Growth Rate:', cell_format)
    calc.write('B5', 'CAGR = (Ending/Beginning)^(1/years) - 1', cell_format)
    
    calc.write('A7', 'QUICK CALCULATORS', header_format)
    calc.write('A8', 'Current Age:', cell_format)
    calc.write('B8', 30, cell_format)
    calc.write('A9', 'Retirement Age:', cell_format)
    calc.write('B9', 60, cell_format)
    calc.write('A10', 'Years to Retirement:', cell_format)
    calc.write('B10', '=B9-B8', cell_format)
    
    calc.write('A12', 'Monthly Surplus Available:', cell_format)
    calc.write('B12', '=Dashboard!B11', currency_format)
    
    calc.write('A13', 'Debt-to-Income Ratio:', cell_format)
    calc.write('B13', '=Liabilities!B15/Income!B15', percentage_format)
    
    calc.write('A14', 'Savings Rate:', cell_format)
    calc.write('B14', '=Dashboard!B11/Income!B15', percentage_format)

    # 8. FLOWCHART SHEET
    flow = workbook.add_worksheet('Financial Flow')
    flow.set_column('A:A', 15)
    flow.set_column('B:B', 20)
    flow.set_column('C:C', 15)
    flow.set_column('D:D', 20)
    
    flow.write('A1', 'FINANCIAL FLOW PROCESS', header_format)
    flow.merge_range('A1:D1', 'FINANCIAL FLOW PROCESS', header_format)
    
    # Create text-based flowchart
    flowchart_steps = [
        ('STEP 1', 'Track Monthly Income', '→', 'Record all income sources'),
        ('STEP 2', 'Track Monthly Expenses', '→', 'Categorize all expenses'),
        ('STEP 3', 'Calculate Surplus', '→', 'Income - Expenses'),
        ('STEP 4', 'List Assets & Liabilities', '→', 'Current financial position'),
        ('STEP 5', 'Define Financial Goals', '→', 'Short & long term goals'),
        ('STEP 6', 'Calculate Future Value', '→', 'Adjust for inflation'),
        ('STEP 7', 'Determine SIP Amount', '→', 'Monthly investment needed'),
        ('STEP 8', 'Allocate Surplus', '→', 'Distribute to goals'),
        ('STEP 9', 'Monitor & Review', '→', 'Monthly tracking'),
        ('STEP 10', 'Adjust Strategy', '→', 'Based on performance')
    ]
    
    for i, (step, action, arrow, description) in enumerate(flowchart_steps, 3):
        flow.write(f'A{i}', step, header_format)
        flow.write(f'B{i}', action, cell_format)
        flow.write(f'C{i}', arrow, cell_format)
        flow.write(f'D{i}', description, cell_format)

    # Close workbook
    workbook.close()
    print("✅ Financial Advisor Excel workbook 'advisor_sri.xlsx' created successfully!")
    print("\n📊 Workbook includes:")
    print("• Dashboard - Financial overview")
    print("• Income - Monthly income tracking")
    print("• Expenses - Monthly expense tracking") 
    print("• Assets - Asset portfolio")
    print("• Liabilities - Debt tracking")
    print("• Goals - Goal planning with SIP calculations")
    print("• Calculations - Financial formulas")
    print("• Financial Flow - Process flowchart")

if __name__ == "__main__":
    create_financial_advisor_workbook()