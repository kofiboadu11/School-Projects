import sys


class Variable:
    def __init__(self, name, domain):
        self.name = name
        self.domain = domain
        self.value = None

class Constraint:
    def __init__(self, var1, op, var2):
        self.var1 = var1
        self.op = op
        self.var2 = var2


def parse_variables(var_file):
    variables = {}
    with open(var_file, 'r') as file:
        for line in file:
            parts = line.strip().split(':')
            var_name = parts[0]
            var_domain = list(map(int, parts[1].split()))
            variables[var_name] = Variable(var_name, var_domain)
    return variables


def parse_constraints(con_file, variables):
    constraints = []
    with open(con_file, 'r') as file:
        for line in file:
            parts = line.strip().split()
            var1 = variables[parts[0]]
            op = parts[1]
            var2 = variables[parts[2]]
            constraints.append(Constraint(var1, op, var2))
    return constraints




def solve_csp(variables, constraints, consistency_enforcing):
    
    def backtrack(assignment):
        
        if all(variable.value is not None for variable in assignment):
            
            print_solution(assignment)
            return True

        
        unassigned = [var for var in assignment if var.value is None]
        unassigned.sort(key=lambda var: len(var.domain))
        selected_var = unassigned[0]

       
        selected_var.domain.sort(key=lambda value: count_conflicts(selected_var, value))

        for value in selected_var.domain:
            
            selected_var.value = value

            if is_consistent(assignment, constraints):
                
                if consistency_enforcing == "fc":
                   
                    if forward_check(assignment, constraints):
                        if backtrack(assignment):
                            return True
                else:
                    if backtrack(assignment):
                        return True

           
            selected_var.value = None

       
        return False

    
    assignment = [var for var in variables.values()]

    
    backtrack(assignment)


def is_consistent(assignment, constraints):
    for constraint in constraints:
        var1 = constraint.var1
        var2 = constraint.var2

        if var1.value is not None and var2.value is not None:
            if constraint.op == "=" and var1.value != var2.value:
                return False
            if constraint.op == "!" and var1.value == var2.value:
                return False
            if constraint.op == ">" and var1.value <= var2.value:
                return False
            if constraint.op == "<" and var1.value >= var2.value:
                return False

    return True


def forward_check(assignment, constraints):
  
    for var in assignment:
        if var.value is None:
            var.domain = [value for value in var.domain if is_consistent(assignment, constraints)]
            if not var.domain:
                return False
    return True


def count_conflicts(variable, value):
    conflicts = 0
    for constraint in constraints:
        if constraint.var1 == variable and constraint.var2.value is not None:
            if constraint.op == "=" and constraint.var2.value == value:
                conflicts += 1
        if constraint.var2 == variable and constraint.var1.value is not None:
            if constraint.op == "=" and constraint.var1.value == value:
                conflicts += 1
    return conflicts


def print_solution(assignment):
   
    variable_assignments = [(var.name, var.value) for var in assignment]
    variable_assignments.sort(key=lambda x: x[0])

    branch_description = ', '.join([f'{var} = {value}' for var, value in variable_assignments])
    print(f'{branch_description} solution')

if __name__ == '__main__':
    if len(sys.argv) != 4:
        print("Usage: python csp_solver.py <.var_file> <.con_file> <consistency_enforcing>")
        sys.exit(1)

    var_file = sys.argv[1]
    con_file = sys.argv[2]
    consistency_enforcing = sys.argv[3]

    variables = parse_variables(var_file)
    constraints = parse_constraints(con_file, variables)

   
    solve_csp(variables, constraints, consistency_enforcing)