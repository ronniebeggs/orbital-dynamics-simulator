import math
import time
import pygame
import sys

#defining global variables/tuples here:
G = 6.67408*(10**-11)

BLACK = (0, 0, 0)
WHITE = (255, 255, 255)
GREY = (200, 200, 200)
RED = (255, 000, 000)
BLUE = (000, 000, 255)
GREEN = (000, 255, 000)
ORANGE = (255, 165, 000)

### PLANET CLASS
class Planet:

    def __init__(self, name, mass, radius, color, parent, orbital_radius, true_anomaly=0, orbital_velo=None):
        #characteristics about the planetary mass
        self.name = name
        self.mass = mass
        self.radius = radius
        self.color = color

        self.parent = parent #planet which the mass orbits around
        self.orbital_radius = orbital_radius #initial distance to the parent planet
        self.orbital_velo = orbital_velo #initial orbital velocity -- if inputed as None, it'll be calculated for a perfectly circular orbit later
        self.true_anomaly = true_anomaly #initial angle of the planet relative to its parent planet

        self.xpos, self.ypos = None, None
        self.xvelo, self.yvelo = None, None

        self.lead_positions = []
        self.lead_velocities = []

    def __repr__(self):
        return self.name

    def planetLead(self, lead_step, i): 
        #calculates the force of gravity between the planet and its parent for a given moment in the future
        #used to create a list of future positions for the planet/craft
        deltax = self.lead_positions[i][0] - self.parent.lead_positions[i][0]
        deltay = self.lead_positions[i][1] - self.parent.lead_positions[i][1]
        distance = math.sqrt((deltax**2) + (deltay**2))
        force_gravity = ((G*self.mass*self.parent.mass) / (1000*distance)**2) / 1000 #G defined globally above
        theta = math.atan2(deltay, deltax) #all angles drawn from the planet

        xaccel = (force_gravity * -math.cos(theta)) / self.mass
        yaccel = (force_gravity * -math.sin(theta)) / self.mass

        #calculates the change in velocity over a given time step interval
        xvelo = self.lead_velocities[i][0] + (xaccel * lead_step) 
        yvelo = self.lead_velocities[i][1] + (yaccel * lead_step) 
        self.lead_velocities.append((xvelo, yvelo))

        #determines the change in position over the same time step interval
        xpos = self.lead_positions[i][0] + (xvelo * lead_step)
        ypos = self.lead_positions[i][1] + (yvelo * lead_step)
        self.lead_positions.append((xpos, ypos))

    def drawPlanet(self):
        if self.name == "Earth":
            pass


### SPACECRAFT CLASS   
class SpaceCraft:

    def __init__(self, name, mass, length, parent, orbital_radius, true_anomaly=0, orbital_velo=None):
        self.name = name
        self.mass = mass
        self.length = length
        
        self.parent = parent #initial planet that the craft orbits around
        self.orbital_radius = orbital_radius #initial distance to the parent planet
        self.orbital_velo = orbital_velo #initial tangential velocity -- if None, first cosmic velocity will be calculated later
        self.true_anomaly = true_anomaly #initial placement of the planet using polar coordinates

        self.xpos, self.ypos = None, None
        self.xvelo, self.yvelo = None, None
        self.rel_xvelo, self.rel_yvelo = None, None

        self.lead_positions = []
        self.adj_lead = []
        self.lead_velocities = []

        self.fuel_remaining = 3000 

    def __repr__(self):
        return self.name

    def craftLead(self, planet_list, lead_step, i):
        #calculates the force of gravity between the craft and all other planets in the simulation
        #used to create a list of future positions for the craft

        #list below stores all gravity vectors in (force, angle) tuple pairs
        gravity_vectors = []
        for planet in planet_list:
        
            deltax = self.lead_positions[i][0] - planet.lead_positions[i][0]
            deltay = self.lead_positions[i][1] - planet.lead_positions[i][1]
            distance = math.sqrt((deltax**2) + (deltay**2))
            #Newton's law of Universal Gravitation takes meters --> additional conversions made inside of the equation
            force_gravity = ((G*craft.mass*planet.mass) / (1000*distance)**2) / 1000
            theta = math.atan2(deltay, deltax)
            gravity_vectors.append((force_gravity, theta))

        #the net force of gravity from all planets is calculated in x and y directions
        net_xforce, net_yforce = 0, 0
        for vector in gravity_vectors:
            net_xforce += vector[0] * -math.cos(vector[1])
            net_yforce += vector[0] * -math.sin(vector[1])

        net_xaccel = net_xforce / self.mass
        net_yaccel = net_yforce / self.mass

        ##calculates the change in velocity over a given time step interval
        xvelo = self.lead_velocities[i][0] + (net_xaccel * lead_step)
        yvelo = self.lead_velocities[i][1] + (net_yaccel * lead_step)	
        self.lead_velocities.append((xvelo, yvelo))

        #determines the change in position over the same time step interval
        xpos = self.lead_positions[i][0] + (xvelo * lead_step)
        ypos = self.lead_positions[i][1] + (yvelo * lead_step)

        self.lead_positions.append((xpos, ypos))

    def drawCraft(self, screen, dp_position, SCALE_FACTOR):
        #calculates the display dimensions of the craft given its total length
        xpos, ypos = dp_position[0], dp_position[1]
        tank_length = (1/(SCALE_FACTOR/200)) * self.length * (2/3)
        tank_width = tank_length / 3
        tank_diagonal = math.sqrt(tank_length**2 + tank_width**2)
        tail_diagonal = math.sqrt((tank_length)**2 + (2*tank_width)**2)
        
        #determines the direction of the craft based on its velocity
        nose_direction = math.atan2(self.yvelo, self.xvelo)
        nose_pos = (xpos + math.cos(nose_direction)*(tank_length*2), ypos + math.sin(nose_direction)*(tank_length*2))

        #p1-4: four edges of the tank in counterclockwise order starting from top right (when the craft is pointed up)
        p1_angle = nose_direction - math.atan2(tank_width, tank_length)
        p2_angle = nose_direction + math.atan2(tank_width, tank_length)
        p3_angle = p1_angle + math.pi
        p4_angle = p2_angle - math.pi
        
        #craft is drawn relative using polar coordinates relative to its display center
        p1_pos = (xpos + math.cos(p1_angle)*(tank_diagonal), ypos + math.sin(p1_angle)*(tank_diagonal))
        p2_pos = (xpos + math.cos(p2_angle)*(tank_diagonal), ypos + math.sin(p2_angle)*(tank_diagonal))
        p3_pos = (xpos + math.cos(p3_angle)*(tank_diagonal), ypos + math.sin(p3_angle)*(tank_diagonal))
        p4_pos = (xpos + math.cos(p4_angle)*(tank_diagonal), ypos + math.sin(p4_angle)*(tank_diagonal))

        #all points are collected 
        tank_points = (p1_pos, p2_pos, p3_pos, p4_pos)
        nose_points = (p1_pos, p2_pos, nose_pos)
        right_tail_points = (
            (xpos + math.cos(nose_direction - math.pi/2)*(tank_width), ypos + math.sin(nose_direction - math.pi/2)*(tank_width)), 
            (xpos + math.cos(nose_direction - 2.5)*(tail_diagonal), ypos + math.sin(nose_direction - 2.5)*(tail_diagonal)), 
            p4_pos
        )
        left_tail_points = (
            (xpos + math.cos(nose_direction + math.pi/2)*(tank_width), ypos + math.sin(nose_direction + math.pi/2)*(tank_width)), 
            (xpos + math.cos(nose_direction + 2.5)*(tail_diagonal), ypos + math.sin(nose_direction + 2.5)*(tail_diagonal)), 
            p3_pos
        )
        #each part of the craft is drawn as separate polygons
        pygame.draw.polygon(screen, RED, nose_points)
        pygame.draw.polygon(screen, RED, right_tail_points)
        pygame.draw.polygon(screen, RED, left_tail_points)
        pygame.draw.polygon(screen, GREY, tank_points)
        #pygame.draw.line(screen, ORANGE, (xpos, ypos), (xpos+tank_length*10*math.cos(nose_direction), ypos+tank_length*10*math.sin(nose_direction)))


### GLOBAL FUNCTIONS          
def scale_converter(real_distance, SCALE_FACTOR):
    #scales all simulation distances to be properly displayed
    scaled_distance = real_distance / SCALE_FACTOR
    return round(scaled_distance)

def fuel_bar(screen, fuel_remaining):
    outline_points = ((10, 10), (120, 10), (120, 30), (10, 30))
    for i, e in enumerate(outline_points):
        if e != outline_points[-1]:
            pygame.draw.line(screen, WHITE, e, outline_points[i+1], 1)
        else:
            pygame.draw.line(screen, WHITE, e, outline_points[0], 1)

    if fuel_remaining <= 0:
        bar_length = 100
        bar_color = RED
    else:
        bar_length = round(fuel_remaining / 3000, 2) * 100
        bar_color = GREEN
    bar_points = ((15, 15), (bar_length+15, 15), (bar_length+15, 25), (15, 25))
    pygame.draw.polygon(screen, bar_color, bar_points)
        
def time_warp_bar(screen, multiplier_options, time_multiplier):
    for i, e in enumerate(multiplier_options):
        pygame.draw.circle(screen, GREEN, (140+i*20, 20), 5)
        if e == time_multiplier:
            break


    









### CLASS INITIALIZATION:

#Planetary Mass: name, mass, radius, parent_planet, orbital_radius
# initialize planets in a decreasing hierarchical order
#sun = Planet("Sun", 1988500*(10**24), 20000, ORANGE, None, 0)
# mercury = Planet("Mercury", 0.330*(10**24), 2439, sun, 57.9*(10**5))
# venus = Planet("Venus", 4.87*(10**24), 6052, sun, 108.2*(10**5))
earth = Planet("Earth", 5.97*(10**24), 6378, BLUE, None, 149.6*(10**6))
moon = Planet("Moon", 0.73*(10**24), 1737, WHITE, earth, 0.384*(10**6)/2)
duna = Planet("Duna", 0.5*(10**24), 2737, ORANGE, earth, 0.384*(10**6))


planet_list = [earth, moon, duna]

#SpaceCraft: name, mass, length, initial_parent, orbital_radius, true_anomaly=0, orbital_velo=None
#if no value inputed for initial velocity, it will assume a circular orbit
craft = SpaceCraft("Craft", 10, 3, earth, 7878, 0.62*math.pi)#, -12)








#parameters: display dimensions, simulation width included in the display window, iteration pause interval
def main(DP_WIDTH, DP_HEIGHT, SIM_WIDTH, physics_fps, lead_length):

    #display infomation and dimensions   
    SCALE_FACTOR = round(SIM_WIDTH / DP_WIDTH) #ratio of km:1 pixel
    TARGET = craft #either a class object or x-y coordinates

    multiplier_options = (1, 10, 100, 1000, 10000) #time warp multiplier options
    time_multiplier = multiplier_options[0] 
    time_step = time_multiplier / physics_fps
    lead_factor = 10000 #ratio of the lead step to the simulations time step
    lead_step = time_step * lead_factor
    thrust_x, thrust_y = 0, 0

    #list of all entities in the simulation
    body_list = []
    for planet in planet_list:
        body_list.append(planet)
    body_list.append(craft) #craft will always have the -1 index
    
    #sets up the initial conditions of the universe relative to some point/planet
    relative_center = None
    for planet in planet_list:
        if planet.parent == None:
            relative_center = planet
            planet.xpos, planet.ypos = 0, 0
            planet.xvelo, planet.yvelo = 0, 0
            break

    #places each body in orbits around their parent planets, everything of which is relative to the center
    for body in body_list:
        #relative center will stay unmoving at the origin of the simulation
        if body == relative_center:
            continue
        #converts polar coordinates into x-y cartesian coordinates and assigns them as an attribute of each body
        body.xpos = body.orbital_radius*math.cos(body.true_anomaly) + body.parent.xpos
        body.ypos = body.orbital_radius*math.sin(body.true_anomaly) + body.parent.ypos

        #converts tangential orbital velocities into x-y velocities and assigns them as an attribute of each body
        if body.orbital_velo == None:
            body.orbital_velo = -1*math.sqrt((G*body.parent.mass)/(1000*body.orbital_radius)) / 1000 #convert m/s to km/s
        body.xvelo = body.orbital_velo*math.cos(body.true_anomaly+math.pi/2) + body.parent.xvelo
        body.yvelo = body.orbital_velo*math.sin(body.true_anomaly+math.pi/2) + body.parent.yvelo

    #initializes planet/craft lead position & velocity lists with values adjusted relative to the center
    for body in body_list:
        body.lead_positions = [(body.xpos, body.ypos)]
        body.lead_velocities = [(body.xvelo, body.yvelo)]

    #initializes pygame library tools and display window
    pygame.init()
    screen = pygame.display.set_mode((DP_WIDTH, DP_HEIGHT))
    
    #EVENT LOOP
    recalculate_lead = True #boolean determining whether to recalculate the lead
    counter = 0 #iteration counter to help understand the speed of each iteration
    running = True
    while running:
        start = time.time()
        # input_start = time.time()
        counter += 1
        #INPUT CALCULATIONS
        #pygame.event list keeps track of all keypress/mouseclicks that occur
        for event in pygame.event.get():
            
            #closes window when exit button pressed
            if event.type == pygame.QUIT:
                running = False

            if event.type == pygame.KEYDOWN:
                
                #zoom in or out using the 1 and 2 number keys, respectively
                if event.key == pygame.K_1:
                    SCALE_FACTOR = round(0.5 * SCALE_FACTOR, 5)
                if event.key == pygame.K_2:
                    SCALE_FACTOR = round(1.5 * SCALE_FACTOR, 5)
                #switch the target object
                if event.key == pygame.K_q:
                    if body_list.index(TARGET)+1 == len(body_list):
                        TARGET = body_list[0]
                    else:
                        TARGET = body_list[body_list.index(TARGET)+1]
                if event.key == pygame.K_e:
                    if body_list.index(TARGET)+1 == len(body_list):
                        TARGET = body_list[0]
                    else:
                        TARGET = body_list[body_list.index(TARGET)-1]

                #changes the simulation speed by altering the simulation time step
                if event.key == pygame.K_z:
                    if multiplier_options.index(time_multiplier)+1 != len(multiplier_options):
                        time_multiplier = multiplier_options[multiplier_options.index(time_multiplier)+1]
                        time_step = time_multiplier / physics_fps
                        lead_factor = lead_step / time_step 

                if event.key == pygame.K_x:
                    if multiplier_options.index(time_multiplier) > 0:
                        time_multiplier = multiplier_options[multiplier_options.index(time_multiplier)-1]
                        time_step = time_multiplier / physics_fps
                        lead_factor = lead_step / time_step
                    
        pressed_keys = pygame.key.get_pressed()
        if pressed_keys[pygame.K_w]:
            #forward/prograde: 1
            direction = 1
        elif pressed_keys[pygame.K_s]:
            #backwards/retrograde: -1
            direction = -1
        else:
            direction = 0
        if direction != 0:
            if craft.fuel_remaining > 0:
                craft_velo = math.sqrt(craft.xvelo**2 + craft.yvelo**2)
                craft_direction = math.atan2(craft.yvelo, craft.xvelo)

                thrust_x = (direction*0.001*craft_velo)*math.cos(craft_direction)
                thrust_y = (direction*0.001*craft_velo)*math.sin(craft_direction)

                craft.fuel_remaining -= 1
                recalculate_lead = True
            else:
                thrust_x, thrust_y = 0, 0
        else:
            thrust_x, thrust_y = 0, 0
        
        # input_stop = time.time()
        # grav_start = time.time()


        #GRAVITY CALCULATIONS
        for planet in planet_list:

            #keeps the relative center at the origin
            if planet == relative_center:
                planet.xpos, planet.ypos = 0, 0
                continue
            #gravity calculated relative to its parent planet
            deltax = planet.xpos - planet.parent.xpos
            deltay = planet.ypos - planet.parent.ypos
            distance = math.sqrt((deltax**2) + (deltay**2))
            force_gravity = ((G*planet.mass*planet.parent.mass) / (1000*distance)**2) / 1000
            theta = math.atan2(deltay, deltax)

            xaccel = (force_gravity * -math.cos(theta)) / planet.mass
            yaccel = (force_gravity * -math.sin(theta)) / planet.mass

            #calculates the change in velocity over a given time step interval
            planet.xvelo += xaccel * time_step
            planet.yvelo += yaccel * time_step 

            #determines the change in position over the same time step interval
            planet.xpos += planet.xvelo * time_step
            planet.ypos += planet.yvelo * time_step
            

        #craft Gravity Calcs
        gravity_vectors = []
        for planet in planet_list:
        
            deltax = craft.xpos - planet.xpos
            deltay = craft.ypos - planet.ypos
            distance = math.sqrt((deltax**2) + (deltay**2))
            force_gravity = ((G*craft.mass*planet.mass) / (1000*distance)**2) / 1000
            theta = math.atan2(deltay, deltax)
            gravity_vectors.append((force_gravity, theta))

        net_xforce, net_yforce = 0, 0
        largest_vector = gravity_vectors[0]
        for vector in gravity_vectors:
                        
            net_xforce += vector[0] * -math.cos(vector[1])
            net_yforce += vector[0] * -math.sin(vector[1])
            
            #the craft's parent is whichever planet exerts the greatest force of gravity upon the craft
            if vector[0] > largest_vector[0]:
                largest_vector = vector

        strongest_influence = planet_list[gravity_vectors.index(largest_vector)]

        net_xaccel = net_xforce / craft.mass
        net_yaccel = net_yforce / craft.mass

        ##calculates the change in velocity over a given time step interval
        craft.xvelo += (net_xaccel * time_step) + thrust_x
        craft.yvelo += (net_yaccel * time_step) + thrust_y
        thrust_x, thrust_y = 0, 0

        #determines the change in position over the same time step interval
        craft.xpos += (craft.xvelo * time_step)
        craft.ypos += (craft.yvelo * time_step)

        relative_velocity = math.sqrt((craft.xvelo - strongest_influence.xvelo)**2 + (craft.yvelo - strongest_influence.yvelo)**2)
        distance_between = math.sqrt((craft.xpos - strongest_influence.xpos)**2 + (craft.ypos - strongest_influence.ypos)**2)
        escape_velocity = math.sqrt(2*G*strongest_influence.mass/(distance_between*1000)) / 1000 
        
        if relative_velocity >= escape_velocity:
            craft.parent = None
            recalculate_lead = True

        if relative_velocity < escape_velocity and craft.parent == None:
            craft.parent = strongest_influence
            recalculate_lead = True
        
        #LEAD CALCULATIONS
        if recalculate_lead == True:
            #wipes the current lead list by overwriting it
            if len(craft.lead_positions) > 1:
                for body in body_list:
                    body.lead_positions = [(body.xpos, body.ypos)] 
                    body.lead_velocities = [(body.xvelo, body.yvelo)]
            
            outside_radius = False
            for i in range(0, lead_length-1):
                #lists storing the future positions of the craft/planet are calculated with a specified length
                for planet in planet_list:

                    if planet == relative_center:
                        planet.lead_positions.append((0, 0))
                        planet.lead_velocities.append((0, 0))
                        continue
                    planet.planetLead(lead_step, i)

                craft.craftLead(planet_list, lead_step, i)

                if craft.parent != None:
                    deltax = craft.lead_positions[i][0] - craft.parent.lead_positions[i][0]
                    deltay = craft.lead_positions[i][1] - craft.parent.lead_positions[i][1]
                    distance = math.sqrt((deltax**2) + (deltay**2))
                    theta = math.atan2(deltay, deltax)
                    
                    adj_xpos = craft.parent.xpos + distance*math.cos(theta)
                    adj_ypos = craft.parent.ypos + distance*math.sin(theta)

                    craft.adj_lead.append((adj_xpos, adj_ypos))
                else:
                    craft.adj_lead.append((craft.lead_positions[i][0], craft.lead_positions[i][1]))

                #the distance between the newest lead position and the current position of the craft are calculated
                #if repetition is calculated, future calculations will cease
                deltax = craft.adj_lead[-1][0] - craft.xpos
                deltay = craft.adj_lead[-1][1] - craft.ypos
                distance = math.sqrt((deltax**2) + (deltay**2))
                 
                if distance > 500 and outside_radius == False:
                    outside_radius = True
                if distance < 500 and outside_radius == True:
                    break                    
            
            #deletes the initial lead point as the craft passes by 
            for body in body_list:
                body.lead_positions.pop(0)
                body.lead_velocities.pop(0)          
                        
        else:
            #lead step > time step --> lead positions are calculated less frequently
            if lead_factor > 1:
                if counter % round(lead_factor) == 0:
                    for planet in planet_list:

                        if planet == relative_center:
                                planet.lead_positions.append((0, 0))
                                planet.lead_velocities.append((0, 0))
                                continue
                        planet.planetLead(lead_step, -1)

                    craft.craftLead(planet_list, lead_step, -1)

            #lead step == time step --> lead calculated at same frequency as actual position
            elif lead_factor == 1:
                for planet in planet_list:

                    if planet == relative_center:
                            planet.lead_positions.append((0, 0))
                            planet.lead_velocities.append((0, 0))
                            continue
                    planet.planetLead(lead_step, -1)

                craft.craftLead(planet_list, lead_step, -1)

            #lead step < time step --> lead positions calculated multiple times per iteration for lead to remain constant length
            else:
                for i in range(0, round(1/lead_factor)):
                    for planet in planet_list:

                        if planet == relative_center:
                                planet.lead_positions.append((0, 0))
                                planet.lead_velocities.append((0, 0))
                                continue
                        planet.planetLead(lead_step, -1)

                    craft.craftLead(planet_list, lead_step, -1)
                
        # grav_stop = time.time()
        # dp_start = time.time()

        #DISPLAY CALCULATIONS

        DP_CENTER_X = DP_WIDTH/2 + (-1 * scale_converter(TARGET.xpos, SCALE_FACTOR))
        DP_CENTER_Y = DP_HEIGHT/2 + (-1 * scale_converter(TARGET.ypos, SCALE_FACTOR))

        screen.fill(BLACK)
        #everything drawn onto a fresh black canvas for every iteration
        for planet in planet_list:
            dp_position = (DP_CENTER_X + scale_converter(planet.xpos, SCALE_FACTOR), DP_CENTER_Y + scale_converter(planet.ypos, SCALE_FACTOR))
            pygame.draw.circle(screen, planet.color, dp_position, scale_converter(planet.radius, SCALE_FACTOR))
            
            if planet != relative_center:
                #draws the circular orbital path of the planet
                for i in range(0, 360):
                    xpos = planet.parent.xpos + planet.orbital_radius*math.cos(math.radians(i))
                    ypos = planet.parent.ypos + planet.orbital_radius*math.sin(math.radians(i))
                    dp_pos = (DP_CENTER_X + scale_converter(xpos, SCALE_FACTOR), DP_CENTER_Y + scale_converter(ypos, SCALE_FACTOR))
                    pygame.draw.circle(screen, planet.color, dp_pos, 1)
            #planet triange marker
            pygame.draw.polygon(screen, ORANGE, [dp_position, [dp_position[0]+5, dp_position[1]-10], [dp_position[0]-5, dp_position[1]-10]])
        
        #draw craft
        craft_dp_pos = (DP_CENTER_X + scale_converter(craft.xpos, SCALE_FACTOR), DP_CENTER_Y + scale_converter(craft.ypos, SCALE_FACTOR))
        craft.drawCraft(screen, craft_dp_pos, SCALE_FACTOR)

        #draw the craft's future path using the lead positions
        for i in range(len(craft.lead_positions)):
            #craft is orbiting around a planet, so their future path relative to the planet is shown
            if craft.parent != None:
                deltax = craft.lead_positions[i][0] - craft.parent.lead_positions[i][0]
                deltay = craft.lead_positions[i][1] - craft.parent.lead_positions[i][1]
                distance = math.sqrt((deltax**2) + (deltay**2))
                theta = math.atan2(deltay, deltax)
                
                adj_xpos = craft.parent.xpos + distance*math.cos(theta)
                adj_ypos = craft.parent.ypos + distance*math.sin(theta)
                lead_color = craft.parent.color
            #craft is currently between planets and their absolute position is displayed
            else:
                adj_xpos = craft.lead_positions[i][0]
                adj_ypos = craft.lead_positions[i][1]
                lead_color = GREY

            dp_xpos = DP_CENTER_X + scale_converter(adj_xpos, SCALE_FACTOR)
            dp_ypos = DP_CENTER_Y + scale_converter(adj_ypos, SCALE_FACTOR)
            pygame.draw.circle(screen, lead_color, (dp_xpos, dp_ypos), 1)
        #craft triangle marker
        pygame.draw.polygon(screen, GREEN, (craft_dp_pos, (craft_dp_pos[0]+5, craft_dp_pos[1]-10), (craft_dp_pos[0]-5, craft_dp_pos[1]-10)))
        
        #lead positions are deleted from the beginning of the list as the craft passes by them
        #lead step > time step --> lead positions will be deleted less frequently
        if lead_factor > 1:
            if counter % round(lead_factor) == 0:
                for body in body_list:
                    body.lead_positions.pop(0)
                    body.lead_velocities.pop(0)
        #lead step == time step --> lead positions will be deleted with each new iteration
        elif lead_factor == 1:
            for body in body_list:
                    body.lead_positions.pop(0)
                    body.lead_velocities.pop(0)
        #lead step < time step --> lead positions will be deleted multiple times per iteration 
        else:
            for i in range(0, round(1/lead_factor)):
                for body in body_list:
                    body.lead_positions.pop(0)
                    body.lead_velocities.pop(0)

        # because the actual simulation and lead calculations are made with different time steps, 
        # the actual position might drift away from the lead
        # if drift (distance) is too larger, the lead is re-calculated
        deltax = craft.lead_positions[0][0] - craft.xpos
        deltay = craft.lead_positions[0][1] - craft.ypos
        distance = math.sqrt((deltax**2) + (deltay**2))
        if distance > 500:
            recalculate_lead = True
        else:
            recalculate_lead = False

        fuel_bar(screen, craft.fuel_remaining)
        time_warp_bar(screen, multiplier_options, time_multiplier)
                
        # dp_stop = time.time()
        
        finish = time.time()
        iteration_length = finish - start

        if counter % 10 and iteration_length > 0:
            pygame.display.set_caption(str(round(1/iteration_length)))
        pygame.display.update()

        # print(f"""
        # Input: {input_stop-input_start}
        # Grav: {grav_stop-grav_start}
        # Display: {dp_stop-dp_start}
        # total: {iteration_length} \n
        # """)

        #pauses based on the iteration length to preserve a desired frame rate 
        iteration_pause = (1/physics_fps) - iteration_length
        if iteration_pause < 0:
            iteration_pause = 0
        time.sleep(iteration_pause)

# screen_height, simulation_width, physics_fps, dp_fps, lead_length
main(1000, 1000, 1000000, 240, 1000)